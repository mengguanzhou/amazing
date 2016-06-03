package com.example.barcodeuploadapp;

public class ScanCodeData {

	private int type;

	private long id;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ScanCodeData [type=" + type + ", id=" + id + "]";
	}

}
