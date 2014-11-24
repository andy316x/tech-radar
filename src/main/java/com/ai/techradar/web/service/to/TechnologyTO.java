package com.ai.techradar.web.service.to;



public class TechnologyTO {

	private Long id;

	private String name;

	private int blipSize;

	private String url;

	private String description;

	private String detailUrl;

	private boolean customerStrategic;

	public TechnologyTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getBlipSize() {
		return blipSize;
	}

	public void setBlipSize(final int blipSize) {
		this.blipSize = blipSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(final String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public boolean isCustomerStrategic() {
		return customerStrategic;
	}

	public void setCustomerStrategic(final boolean customerStrategic) {
		this.customerStrategic = customerStrategic;
	}

}
