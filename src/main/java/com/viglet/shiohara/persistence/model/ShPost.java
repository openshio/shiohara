package com.viglet.shiohara.persistence.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the ShPost database table.
 * 
 */
@Entity
@NamedQuery(name="ShPost.findAll", query="SELECT s FROM ShPost s")
public class ShPost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String summary;

	private String title;

	//bi-directional many-to-one association to ShPostType
	@ManyToOne
	@JoinColumn(name="post_type_id")
	private ShPostType shPostType;

	//bi-directional many-to-one association to ShPostAttr
	@OneToMany(mappedBy="shPost")
	private List<ShPostAttr> shPostAttrs;

	//bi-directional many-to-one association to ShRegion
	@OneToMany(mappedBy="shPost")
	private List<ShRegion> shRegions;

	public ShPost() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ShPostType getShPostType() {
		return this.shPostType;
	}

	public void setShPostType(ShPostType shPostType) {
		this.shPostType = shPostType;
	}

	public List<ShPostAttr> getShPostAttrs() {
		return this.shPostAttrs;
	}

	public void setShPostAttrs(List<ShPostAttr> shPostAttrs) {
		this.shPostAttrs = shPostAttrs;
	}

	public ShPostAttr addShPostAttr(ShPostAttr shPostAttr) {
		getShPostAttrs().add(shPostAttr);
		shPostAttr.setShPost(this);

		return shPostAttr;
	}

	public ShPostAttr removeShPostAttr(ShPostAttr shPostAttr) {
		getShPostAttrs().remove(shPostAttr);
		shPostAttr.setShPost(null);

		return shPostAttr;
	}

	public List<ShRegion> getShRegions() {
		return this.shRegions;
	}

	public void setShRegions(List<ShRegion> shRegions) {
		this.shRegions = shRegions;
	}

	public ShRegion addShRegion(ShRegion shRegion) {
		getShRegions().add(shRegion);
		shRegion.setShPost(this);

		return shRegion;
	}

	public ShRegion removeShRegion(ShRegion shRegion) {
		getShRegions().remove(shRegion);
		shRegion.setShPost(null);

		return shRegion;
	}

}