package io.hychou.file.entity;

import java.util.Date;

public interface FileInfo {
    Long getId();

    String getName();

    Date getLastModified();
}
