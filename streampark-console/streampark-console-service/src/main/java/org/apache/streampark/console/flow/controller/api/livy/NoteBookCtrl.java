package org.apache.streampark.console.flow.controller.api.livy;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.livy.service.INoteBookService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.NoteBookVoRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "noteBoot api", tags = "noteBoot api")
@RestController
@RequestMapping(value = "/noteBoot")
public class NoteBookCtrl {

  private final INoteBookService noteBookServiceImpl;
  private final ILogHelperService logHelperServiceImpl;

  @Autowired
  public NoteBookCtrl(
      INoteBookService noteBookServiceImpl, ILogHelperService logHelperServiceImpl) {
    this.noteBookServiceImpl = noteBookServiceImpl;
    this.logHelperServiceImpl = logHelperServiceImpl;
  }

  @RequestMapping(value = "/saveOrUpdateNoteBook", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "saveOrUpdateNoteBook", notes = "save or update NoteBook")
  public String saveOrUpdateNoteBook(NoteBookVoRequest noteBookVo) throws Exception {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed(
        "saveOrUpdateNoteBook " + noteBookVo.getName(), currentUsername);
    return noteBookServiceImpl.saveOrUpdateNoteBook(currentUsername, isAdmin, noteBookVo, false);
  }

  @RequestMapping(value = "/getNoteBookById", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "getNoteBookById", notes = "get NoteBook by id")
  @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query")
  public String getNoteBookById(String id) throws Exception {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return noteBookServiceImpl.getNoteBookById(currentUsername, isAdmin, id);
  }

  @RequestMapping(value = "/checkNoteBookName", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "checkNoteBookName", notes = "check NoteBook name")
  @ApiImplicitParam(
      name = "noteBookName",
      value = "noteBookName",
      required = true,
      paramType = "query")
  public String checkNoteBookName(String noteBookName) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return noteBookServiceImpl.checkNoteBookName(currentUsername, isAdmin, noteBookName);
  }

  @RequestMapping(value = "/deleteNoteBook", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "deleteNoteBook", notes = "delete NoteBook")
  @ApiImplicitParam(name = "noteBookId", value = "noteBookId", required = true, paramType = "query")
  public String deleteNoteBook(String noteBookId) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("deleteNoteBook " + noteBookId, currentUsername);
    return noteBookServiceImpl.deleteNoteBook(currentUsername, isAdmin, noteBookId);
  }

  @RequestMapping(value = "/noteBookListPage", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "noteBookListPage", notes = "NoteBook list")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
    @ApiImplicitParam(name = "limit", value = "limit", required = true, paramType = "query"),
    @ApiImplicitParam(name = "param", value = "param", required = false, paramType = "query")
  })
  public String noteBookListPage(Integer page, Integer limit, String param) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return noteBookServiceImpl.getNoteBookListPage(currentUsername, isAdmin, page, limit, param);
  }

  @RequestMapping(value = "/startNoteBookSession", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "startNoteBookSession", notes = "start NoteBook session")
  @ApiImplicitParam(name = "noteBookId", value = "noteBookId", required = true, paramType = "query")
  public String startNoteBookSession(String noteBookId) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("startNoteBookSession " + noteBookId, currentUsername);
    return noteBookServiceImpl.startNoteBookSession(currentUsername, isAdmin, noteBookId);
  }

  @RequestMapping(value = "/getNoteBookSessionState", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "startNoteBookSession", notes = "start NoteBook session")
  @ApiImplicitParam(name = "noteBookId", value = "noteBookId", required = true, paramType = "query")
  public String getNoteBookSessionState(String noteBookId) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return noteBookServiceImpl.getNoteBookSessionState(currentUsername, isAdmin, noteBookId);
  }

  @RequestMapping(value = "/deleteNoteBookSession", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "deleteNoteBookSession", notes = "delete NoteBook session")
  @ApiImplicitParam(name = "noteBookId", value = "noteBookId", required = true, paramType = "query")
  public String deleteNoteBookSession(String noteBookId) {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("deleteNoteBookSession " + noteBookId, currentUsername);
    return noteBookServiceImpl.delNoteBookSession(currentUsername, isAdmin, noteBookId);
  }
}
