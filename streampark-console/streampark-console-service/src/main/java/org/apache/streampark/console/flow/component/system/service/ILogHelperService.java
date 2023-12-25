package org.apache.streampark.console.flow.component.system.service;

import org.springframework.stereotype.Service;

@Service
public interface ILogHelperService {

  void logAuthSucceed(String action, String result);

  void logAdmin(Integer type, String action, Boolean succeed, String result, String comment);
}
