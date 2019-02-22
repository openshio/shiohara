var shNavigationComponent = spring.getBean('shNavigationComponent', Java
		.type('com.viglet.shiohara.component.ShNavigationComponent'));
var shQueryComponent = spring.getBean('shQueryComponent', Java
		.type('com.viglet.shiohara.component.ShQueryComponent'));
var shSearchComponent = spring.getBean('shSearchComponent', Java
		.type('com.viglet.shiohara.component.ShSearchComponent'));
var shFormComponent = spring.getBean('shFormComponent', Java
		.type('com.viglet.shiohara.component.form.ShFormComponent'));
var shFolderUtils = spring.getBean('shFolderUtils', Java
		.type('com.viglet.shiohara.utils.ShFolderUtils'));
var shObjectUtils = spring.getBean('shObjectUtils', Java
		.type('com.viglet.shiohara.utils.ShObjectUtils'));
var shPostUtils = spring.getBean('shPostUtils', Java
		.type('com.viglet.shiohara.utils.ShPostUtils'));
var shGetRelationComponent = spring.getBean('shGetRelationComponent', Java
		.type('com.viglet.shiohara.component.ShGetRelationComponent'));

var viglet = this.viglet || {};
viglet.shiohara = viglet.shiohara || {};

/**
 * @desc the shObject class. See usage.
 * 
 * @class
 * @extends viglet.shiohara
 */
viglet.shiohara.shObject = function() {
	/**
	 * @desc Returns Form from PostType
	 * @public
	 */
	this.formComponent = function(shPostTypeName, shObjectId) {
		return shFormComponent.byPostType(shPostTypeName, shObjectId, request);
	},	
	/**
	 * @desc Returns Search Result
	 * @public
	 */
	this.searchComponent = function() {
		return Java.from(shSearchComponent.search(request.getParameter('q')));
	},

	/**
	 * @desc Returns Folder Navigation Component
	 * @param siteName,
	 *            Site Name.
	 * @param home,
	 *            true or false to show the Home folder.
	 * @public
	 */
	this.navigation = function(siteName, home) {
		return Java.from(shNavigationComponent.navigation(siteName, home));
	},

	/**
	 * @desc Returns Folder Navigation Component from Parent Folder
	 * @param folderId,
	 *            Folder Id.
	 * @param home,
	 *            true or false to show the Home folder.
	 * @public
	 */
	this.navigationFolder = function(folderId, home) {
		return Java
				.from(shNavigationComponent.navigationFolder(folderId, home));
	},
	/**
	 * @desc Returns Query Component
	 * @param folderId,
	 *            Folder Id.
	 * @param postTypeName,
	 *            Post Type Name.
	 * @public
	 */
	this.query = function(folderId, postTypeName) {
		return Java.from(shQueryComponent.findByFolderName(folderId,
				postTypeName));
	}

	/**
	 * @desc Returns Query Component
	 * @param postTypeName,
	 *            Post Type Name.
	 * @param postAttrName,
	 *            Post Type Attribute Name.
	 * @param arrayValue,
	 *            Array Value.            
	 * @public
	 */
	this.queryByPostTypeIn = function(postTypeName, postAttrName, arrayValue) {
		return Java.from(shQueryComponent.findByPostTypeNameIn(postTypeName, postAttrName, arrayValue));
	}
	
	/**
	 * @desc Returns getRelation Component
	 * @param shPostAttrId,
	 *            Post Attribute Id.
	 * @public
	 */
	this.getRelation = function(shPostAttrId) {
		return Java.from(shGetRelationComponent.findByPostAttrId(shPostAttrId));
	}
	
	/**
	 * @desc Generate Post Link
	 * @param postId,
	 *            Post Id.
	 * @public
	 */
	this.generatePostLink = function(postId) {
		return shPostUtils.generatePostLinkById(postId);
	}

	/**
	 * @desc Generate Folder Link
	 * @param folderId,
	 *            Folder Id.
	 * @public
	 */
	this.generateFolderLink = function(folderId) {
		return shFolderUtils.generateFolderLinkById(folderId);
	}

	/**
	 * @desc Get Post Map
	 * @param postId,
	 *            Post Id.
	 * @public
	 */
	this.getPost = function(postId) {
		return shPostUtils.toMap(postId);
	}
	
	/**
	 * @desc Get Folder Map
	 * @param folderId,
	 *            Folder Id.
	 * @public
	 */
	this.getFolderMap = function(folderId) {
		return shFolderUtils.toMap(folderId);
	}
	
	/**
	 * @desc Get Parent Folder Map
	 * @param folderId,
	 *            Folder Id.
	 * @public
	 */
	this.getParentFolder = function(folderId) {
		return shFolderUtils.toMap(shFolderUtils.getParentFolder(folderId));
	}
	/**
	 * @desc Generate Object Link
	 * @param objectId,
	 *            Object Id.
	 * @public
	 */
	this.generateObjectLink = function(objectId) {
		return shObjectUtils.generateObjectLinkById(objectId);
	}

};

viglet.shiohara.shobject = new viglet.shiohara.shObject();
