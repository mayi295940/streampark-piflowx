package org.apache.streampark.console.flow.component.livy.util;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.livy.entity.NoteBook;

import java.util.Date;

public class NoteBookUtils {

  public static NoteBook setNoteBookBasicInformation(
      NoteBook noteBook, boolean isSetId, String username) {
    if (null == noteBook) {
      noteBook = new NoteBook();
    }
    if (isSetId) {
      noteBook.setId(UUIDUtils.getUUID32());
    }
    noteBook.setCrtDttm(new Date());
    noteBook.setCrtUser(username);
    noteBook.setLastUpdateDttm(new Date());
    noteBook.setLastUpdateUser(username);
    noteBook.setVersion(0L);
    return noteBook;
  }
}
