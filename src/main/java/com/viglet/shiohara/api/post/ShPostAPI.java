package com.viglet.shiohara.api.post;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viglet.shiohara.persistence.model.post.ShPostAttr;
import com.viglet.shiohara.persistence.model.reference.ShReference;
import com.viglet.shiohara.persistence.model.user.ShUser;
import com.fasterxml.jackson.annotation.JsonView;
import com.viglet.shiohara.api.ShJsonView;
import com.viglet.shiohara.persistence.model.globalid.ShGlobalId;
import com.viglet.shiohara.persistence.model.object.ShObject;
import com.viglet.shiohara.persistence.model.post.ShPost;
import com.viglet.shiohara.persistence.repository.globalid.ShGlobalIdRepository;
import com.viglet.shiohara.persistence.repository.post.ShPostAttrRepository;
import com.viglet.shiohara.persistence.repository.post.ShPostRepository;
import com.viglet.shiohara.persistence.repository.reference.ShReferenceRepository;
import com.viglet.shiohara.persistence.repository.user.ShUserRepository;
import com.viglet.shiohara.utils.ShStaticFileUtils;

@RestController
@RequestMapping("/api/v2/post")
public class ShPostAPI {

	@Autowired
	private ShPostRepository shPostRepository;
	@Autowired
	private ShPostAttrRepository shPostAttrRepository;
	@Autowired
	private ShUserRepository shUserRepository;
	@Autowired
	private ShStaticFileUtils shStaticFileUtils;
	@Autowired
	private ShGlobalIdRepository shGlobalIdRepository;
	@Autowired
	private ShReferenceRepository shReferenceRepository;

	@RequestMapping(method = RequestMethod.GET)
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public List<ShPost> shPostList() throws Exception {
		return shPostRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public ShPost shPostEdit(@PathVariable UUID id) throws Exception {
		ShPost shPost = shPostRepository.findById(id);
		shPost.setShPostAttrs(shPostAttrRepository.findByShPost(shPost));
		return shPost;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public ShPost shPostUpdate(@PathVariable UUID id, @RequestBody ShPost shPost) throws Exception {

		ShPost shPostEdit = shPostRepository.findById(id);

		String title = shPostEdit.getTitle();
		String summary = shPostEdit.getSummary();

		for (ShPostAttr shPostAttr : shPost.getShPostAttrs()) {

			if (shPostAttr.getShPostTypeAttr().getIsTitle() == 1)
				title = StringUtils.abbreviate(shPostAttr.getStrValue(), 255);

			if (shPostAttr.getShPostTypeAttr().getIsSummary() == 1)
				summary = StringUtils.abbreviate(shPostAttr.getStrValue(), 255);

			ShPostAttr shPostAttrEdit = shPostAttrRepository.findById(shPostAttr.getId());
			this.referencedFile(shPostAttrEdit, shPostAttr, shPost);

			if (shPostAttrEdit != null) {
				shPostAttrEdit.setDateValue(shPostAttr.getDateValue());
				shPostAttrEdit.setIntValue(shPostAttr.getIntValue());
				shPostAttrEdit.setStrValue(shPostAttr.getStrValue());
				shPostAttrEdit.setReferenceObjects(shPostAttr.getReferenceObjects());
				shPostAttrRepository.saveAndFlush(shPostAttrEdit);
			}
		}
		shPostEdit = shPostRepository.findById(id);

		shPostEdit.setDate(new Date());
		shPostEdit.setTitle(title);
		shPostEdit.setSummary(summary);

		shPostRepository.saveAndFlush(shPostEdit);

		ShUser shUser = shUserRepository.findById(1);
		shUser.setLastPostType(String.valueOf(shPostEdit.getShPostType().getId()));
		shUserRepository.saveAndFlush(shUser);
		
		// Lazy
		shPostEdit.setShPostAttrs(shPostAttrRepository.findByShPost(shPostEdit));
		
		return shPostEdit;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public boolean shPostDelete(@PathVariable UUID id) throws Exception {

		ShPost shPost = shPostRepository.findById(id);
		List<ShPostAttr> shPostAttrs = shPostAttrRepository.findByShPost(shPost);
		if (shPost.getShPostType().getName().equals("PT-FILE") && shPostAttrs.size() > 0) {
			File file = shStaticFileUtils.filePath(shPost.getShFolder(), shPostAttrs.get(0).getStrValue());
			if (file != null) {
				if (file.exists()) {
					file.delete();
				}
			}
		}
		
		for (ShPostAttr shPostAttr : shPostAttrs) {
			shPostAttrRepository.delete(shPostAttr.getId());
		}

		for (ShReference shReference : shReferenceRepository.findByShGlobalFromId(shPost.getShGlobalId())) {
			shReferenceRepository.delete(shReference.getId());
		}
		for (ShReference shReference : shReferenceRepository.findByShGlobalToId(shPost.getShGlobalId())) {
			shReferenceRepository.delete(shReference.getId());
		}

		shGlobalIdRepository.delete(shPost.getShGlobalId().getId());

		shPostRepository.delete(id);

		return true;
	}

	@RequestMapping(method = RequestMethod.POST)
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public ShPost shPostAdd(@RequestBody ShPost shPost) throws Exception {

		String title = shPost.getTitle();
		String summary = shPost.getSummary();
		// Get PostAttrs before save, because JPA Lazy
		List<ShPostAttr> shPostAttrs = shPost.getShPostAttrs();
		for (ShPostAttr shPostAttr : shPost.getShPostAttrs()) {
			if (shPostAttr.getShPostTypeAttr().getIsTitle() == 1)
				title = StringUtils.abbreviate(shPostAttr.getStrValue(), 255);

			if (shPostAttr.getShPostTypeAttr().getIsSummary() == 1)
				summary = StringUtils.abbreviate(shPostAttr.getStrValue(), 255);

			if (shPostAttr != null) {
				shPostAttr.setReferenceObjects(null);

			}
		}
		shPost.setDate(new Date());
		shPost.setTitle(title);
		shPost.setSummary(summary);

		shPostRepository.saveAndFlush(shPost);

		ShGlobalId shGlobalId = new ShGlobalId();
		shGlobalId.setShObject(shPost);
		shGlobalId.setType("POST");

		shGlobalIdRepository.saveAndFlush(shGlobalId);

		ShPost shPostWithGlobalId = shPostRepository.findById(shPost.getId());

		
		for (ShPostAttr shPostAttr : shPostAttrs) {
			shPostAttr.setShPost(shPostWithGlobalId);
			this.referencedFile(shPostAttr, shPostAttr, shPostWithGlobalId);
			shPostAttrRepository.saveAndFlush(shPostAttr);
		}

		shPostRepository.saveAndFlush(shPost);

		ShUser shUser = shUserRepository.findById(1);
		shUser.setLastPostType(String.valueOf(shPost.getShPostType().getId()));
		shUserRepository.saveAndFlush(shUser);

		// Lazy

		
		return shPost;

	}

	public void referencedFile(ShPostAttr shPostAttrEdit, ShPostAttr shPostAttr, ShPost shPost) {
		if (shPostAttrEdit.getShPostTypeAttr().getShWidget().getName().equals("File")) {

			if (shPost.getShPostType().getName().equals("PT-FILE")) {
				File fileFrom = shStaticFileUtils.filePath(shPost.getShFolder(), shPostAttrEdit.getStrValue());
				File fileTo = shStaticFileUtils.filePath(shPost.getShFolder(), shPostAttr.getStrValue());
				if (fileFrom != null && fileTo != null) {
					if (fileFrom.exists()) {
						fileFrom.renameTo(fileTo);
					}
				}

			} else {
				if (shPostAttr.getStrValue() == null) {
					shPostAttr.setReferenceObjects(null);
				} else {
					ShPost shPostFile = shPostRepository.findById(UUID.fromString(shPostAttr.getStrValue()));
					// TODO Two or more attributes with FILE Widget and same file, it cannot remove
					// a valid reference
					// Remove old references
					List<ShReference> shOldReferences = shReferenceRepository
							.findByShGlobalFromId(shPost.getShGlobalId());
					if (shOldReferences.size() > 0) {
						// System.out.println("Removing old references");
						for (ShReference shOldReference : shOldReferences) {
							// System.out.println("Old Reference: " +
							// shOldReference.getShGlobalFromId().getId() + ", "
							// + shOldReference.getShGlobalToId().getId());
							if (shPostAttrEdit.getReferenceObjects() != null) {
								for (ShObject shObject : shPostAttrEdit.getReferenceObjects()) {
									// System.out.println("shObject: " +
									// shObject.getShGlobalId().getId().toString());
									if (shOldReference.getShGlobalToId().getId().toString()
											.equals(shObject.getShGlobalId().getId().toString())) {
										shReferenceRepository.delete(shOldReference);
										// System.out.println("Reference removed");
										break;
									}
								}
							}
						}
					}

					// Create new reference
					ShReference shReference = new ShReference();
					shReference.setShGlobalFromId(shPost.getShGlobalId());
					shReference.setShGlobalToId(shPostFile.getShGlobalId());
					shReferenceRepository.saveAndFlush(shReference);

					Set<ShObject> referenceObjects = new HashSet<ShObject>();
					referenceObjects.add(shPostFile);
					shPostAttr.setReferenceObjects(referenceObjects);
				}
			}

		}
	}
}
