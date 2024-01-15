package org.apache.streampark.console.flow.component.livy.mapper;

import org.apache.streampark.console.flow.component.livy.entity.NoteBook;
import org.apache.streampark.console.flow.component.livy.mapper.provider.NoteBookMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface NoteBookMapper {

  /**
   * add NoteBook
   *
   * @param noteBook noteBook
   * @return Integer
   */
  @InsertProvider(type = NoteBookMapperProvider.class, method = "addNoteBook")
  Integer addNoteBook(NoteBook noteBook);

  /**
   * update NoteBook
   *
   * @param noteBook noteBook
   * @return Integer
   */
  @UpdateProvider(type = NoteBookMapperProvider.class, method = "updateNoteBook")
  Integer updateNoteBook(NoteBook noteBook);

  /**
   * update NoteBook enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param id id
   * @return Integer
   */
  @UpdateProvider(type = NoteBookMapperProvider.class, method = "deleteNoteBookById")
  Integer deleteNoteBookById(boolean isAdmin, String username, String id);

  /**
   * get NoteBook by name
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param name name
   * @return Integer
   */
  @SelectProvider(type = NoteBookMapperProvider.class, method = "checkNoteBookByName")
  Integer checkNoteBookByName(boolean isAdmin, String username, String name);

  /**
   * get NoteBook by id
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param id id
   * @return Integer
   */
  @SelectProvider(type = NoteBookMapperProvider.class, method = "getNoteBookById")
  NoteBook getNoteBookById(boolean isAdmin, String username, String id);

  /**
   * get NoteBook by id
   *
   * @param id id
   * @return Integer
   */
  @SelectProvider(type = NoteBookMapperProvider.class, method = "adminGetNoteBookById")
  NoteBook adminGetNoteBookById(String id);

  /**
   * search NoteBook List
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   */
  @SelectProvider(type = NoteBookMapperProvider.class, method = "getNoteBookList")
  List<NoteBook> getNoteBookList(boolean isAdmin, String username, String param);
}
