package com.mrh.qspl.io.extension;

public interface Extension {
	public String getName();
	public String getAuthor();
	public String getDescription();
	public void extend(ExtensionScope ext);
}
