package org.apache.streampark.console.flow.base.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.slf4j.Logger;

/** Http tool class */
public class HttpUtils {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static final Logger logger = LoggerUtil.getLogger();

  /**
   * "post" request to transfer "json" data
   *
   * @param url url
   * @param json json
   * @param timeOutMS (Millisecond)
   */
  public static String doPostParmaMap(String url, Map<?, ?> json, Integer timeOutMS) {
    if (null == json) {
      return doPost(url, null, timeOutMS);
    }
    String formatJson = JsonUtils.toFormatJsonNoException(json);
    return doPost(url, formatJson, timeOutMS);
  }

  /**
   * "post" request to transfer "json" data
   *
   * @param url url
   * @param json json
   * @param timeOutMS (Millisecond)
   */
  public static String doPost(String url, String json, Integer timeOutMS) {
    return doPostComCustomizeHeader(url, json, timeOutMS, null);
  }

  /**
   * Get request to transfer data
   *
   * @param url url
   * @param map Request incoming parameters ("key" is the parameter name, "value" is the parameter
   *     value) "map" can be empty
   * @param timeOutMS (Millisecond)
   */
  public static String doGet(String url, Map<String, String> map, Integer timeOutMS) {
    if (null == map || map.isEmpty()) {
      return doGetComCustomizeHeader(url, null, timeOutMS, null);
    }
    Map<String, Object> mapObject = new HashMap<>();
    for (String key : map.keySet()) {
      mapObject.put(key, map.get(key));
    }
    return doGetComCustomizeHeader(url, mapObject, timeOutMS, null);
  }

  public static String getHtml(String urlStr) {

    // Define links to be accessed
    // Define a string to store web content
    StringBuilder result = new StringBuilder();
    // Define a buffered character input stream
    BufferedReader in = null;
    try {
      // Convert string to url object
      URL realUrl = new URL(urlStr);
      // Initialize a link to the "url" link
      URLConnection connection = realUrl.openConnection();
      // Start the actual connection
      connection.connect();
      // Initialize the "BufferedReader" input stream to read the response of the "URL"
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      // Used to temporarily store data for each fetched row
      String line;
      while ((line = in.readLine()) != null) {
        // Traverse each row that is fetched and store it in "result"
        result.append(line).append("\n");
      }
    } catch (Exception e) {
      logger.error("send get request is abnormal!" + e);
      e.printStackTrace();
    } // Use "finally" to close the input stream
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    logger.debug("html info:" + result);
    return result.toString();
  }

  /**
   * "post" request to transfer "json" data
   *
   * @param url url
   * @param timeOutMS (Millisecond)
   */
  public static String doDelete(String url, Integer timeOutMS) {
    String result;

    CloseableHttpClient httpClient;
    // Create a "post" mode request object
    HttpDelete httpDelete = null;
    try {
      httpClient = HttpClients.createDefault();
      httpDelete = new HttpDelete(url);
      httpDelete.setProtocolVersion(HttpVersion.HTTP_1_1);
      if (null != timeOutMS) {
        // Set timeout
        RequestConfig requestConfig =
            RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(timeOutMS)
                .build();
        httpDelete.setConfig(requestConfig);
      }

      logger.info("call '" + url + "' start");
      // Perform the request operation and get the result (synchronous blocking)
      CloseableHttpResponse response = httpClient.execute(httpDelete);
      logger.info("call succeeded,return msg:" + response.toString());
      // Get result entity
      // Determine whether the network connection status code is normal (0--200 are normal)
      switch (response.getStatusLine().getStatusCode()) {
        case HttpStatus.SC_OK:
        case HttpStatus.SC_CREATED:
          result = EntityUtils.toString(response.getEntity(), "utf-8");
          logger.info("call succeeded,return msg:" + result);
          break;
        default:
          result =
              MessageConfig.INTERFACE_CALL_ERROR_MSG()
                  + ":"
                  + EntityUtils.toString(response.getEntity(), "utf-8");
          logger.warn("call failed,return msg:" + result);
          break;
      }
    } catch (UnsupportedCharsetException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":UnsupportedCharsetException");
    } catch (ClientProtocolException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ClientProtocolException");
    } catch (ParseException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ParseException");
    } catch (IOException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":IOException");
    } finally {
      // Close the connection and release the resource
      if (httpDelete != null) {
        httpDelete.releaseConnection();
      }
    }
    return result;
  }

  public static String doPostComCustomizeHeader(
      String url, String json, Integer timeOutMS, Map<String, String> herderParam) {
    String result;

    // Create a "post" mode request object
    HttpPost httpPost = null;
    try {
      // Create a "post" mode request object
      httpPost = new HttpPost(url);
      logger.debug("afferent json param:" + json);
      // Set parameters to the request object
      if (StringUtils.isNotBlank(json)) {
        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding("utf-8");
        httpPost.setEntity(stringEntity);
      }
      httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
      if (null != timeOutMS) {
        // Set timeout
        RequestConfig requestConfig =
            RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(timeOutMS)
                .build();
        httpPost.setConfig(requestConfig);
      }
      // add header param
      if (null != herderParam && herderParam.keySet().size() > 0) {
        for (String key : herderParam.keySet()) {
          if (null == key) {
            continue;
          }
          httpPost.addHeader(key, herderParam.get(key));
        }
      }

      logger.info("call '" + url + "' start");
      result = doPostComCustomizeHttpPost(httpPost);
    } catch (UnsupportedCharsetException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":UnsupportedCharsetException");
    } catch (ParseException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ParseException");
    } finally {
      // Close the connection and release the resource
      if (httpPost != null) {
        httpPost.releaseConnection();
      }
    }
    return result;
  }

  public static String doPostComCustomizeHeaderAndFile(
      String url,
      Map<String, String> herderParam,
      Map<String, String> params,
      File file,
      Integer timeOutMS) {

    String result;
    HttpPost httpPost = null;

    try {
      // Create a "post" mode request object
      httpPost = new HttpPost(url);
      MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
      if (null != file && file.exists()) {
        multipartEntityBuilder.addBinaryBody("file", file);
        multipartEntityBuilder.addTextBody("comment", "this is comment");
      }
      if (null != params && !params.isEmpty()) {
        for (String key : params.keySet()) {
          multipartEntityBuilder.addTextBody(key, params.get(key));
        }
      }
      HttpEntity httpEntity = multipartEntityBuilder.build();
      httpPost.setEntity(httpEntity);
      httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
      if (null != timeOutMS) {
        // Set timeout
        RequestConfig requestConfig =
            RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(timeOutMS)
                .build();
        httpPost.setConfig(requestConfig);
      }
      // add header param
      if (null != herderParam && herderParam.keySet().size() > 0) {
        for (String key : herderParam.keySet()) {
          if (null == key) {
            continue;
          }
          httpPost.addHeader(key, herderParam.get(key));
        }
      }
      logger.info("call '" + url + "' start");
      result = doPostComCustomizeHttpPost(httpPost);
    } catch (UnsupportedCharsetException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":UnsupportedCharsetException");
    } catch (ParseException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ParseException");
    } finally {
      // Close the connection and release the resource
      if (httpPost != null) {
        httpPost.releaseConnection();
      }
    }
    return result;
  }

  public static String doPostComCustomizeHttpPost(HttpPost httpPost) {
    if (null == httpPost) {
      return (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":HttpPost is Null");
    }
    String result;
    CloseableHttpClient httpClient;

    try {
      httpClient = HttpClients.createDefault();
      // Perform the request operation and get the result (synchronous blocking)
      CloseableHttpResponse response = httpClient.execute(httpPost);
      logger.info("call succeeded,return msg:" + response.toString());
      // Get result entity
      // Determine whether the network connection status code is normal (0--200 are normal)
      switch (response.getStatusLine().getStatusCode()) {
        case HttpStatus.SC_OK:
        case HttpStatus.SC_CREATED:
          result = EntityUtils.toString(response.getEntity(), "utf-8");
          logger.info("call succeeded,return msg:" + result);
          break;
        default:
          result =
              MessageConfig.INTERFACE_CALL_ERROR_MSG()
                  + ":"
                  + EntityUtils.toString(response.getEntity(), "utf-8");
          logger.warn("call failed,return msg:" + result);
          break;
      }
    } catch (UnsupportedCharsetException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":UnsupportedCharsetException");
    } catch (ClientProtocolException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ClientProtocolException");
    } catch (ParseException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ParseException");
    } catch (IOException e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":IOException");
    } catch (Exception e) {
      logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":Exception");
    }
    return result;
  }

  public static String doGetComCustomizeHeader(
      String url, Map<String, Object> map, Integer timeOutMS, Map<String, String> herderParam) {
    String result = "";
    if (StringUtils.isNotBlank(url)) {
      try {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet;
        // Determine whether to add parameters
        if (null != map && !map.isEmpty()) {
          // Since the parameters of the "GET" request are all assembled behind the "URL" address,
          // we have to build a "URL" with parameters.

          URIBuilder uriBuilder = new URIBuilder(url);

          List<NameValuePair> list = new LinkedList<>();
          for (String key : map.keySet()) {
            BasicNameValuePair param = new BasicNameValuePair(key, map.get(key).toString());
            list.add(param);
          }

          uriBuilder.setParameters(list);
          // Construct a "GET" request object from a "URI" object with parameters
          httpGet = new HttpGet(uriBuilder.build());
        } else {
          httpGet = new HttpGet(url);
        }

        // Type of transmission
        httpGet.addHeader("Content-type", "application/json");

        // Add request header information
        // Browser representation
        // httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1;
        // en-US; rv:1.7.6)");
        // Type of transmission
        // httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // add header param
        if (null != herderParam && herderParam.keySet().size() > 0) {
          for (String key : herderParam.keySet()) {
            if (null == key) {
              continue;
            }
            httpGet.addHeader(key, herderParam.get(key));
          }
        }
        if (null != timeOutMS) {
          // Set timeout
          RequestConfig requestConfig =
              RequestConfig.custom()
                  .setConnectTimeout(5000)
                  .setConnectionRequestTimeout(1000)
                  .setSocketTimeout(timeOutMS)
                  .build();
          httpGet.setConfig(requestConfig);
        }

        logger.info("call '" + url + "' start");
        // Get the response object by requesting the object
        CloseableHttpResponse response = httpClient.execute(httpGet);
        logger.info("call succeeded,return msg:" + response.toString());
        // Get result entity
        // Determine whether the network connection status code is normal (0--200 are normal)
        switch (response.getStatusLine().getStatusCode()) {
          case HttpStatus.SC_OK:
          case HttpStatus.SC_CREATED:
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info("call succeeded,return msg:" + result);
            break;
          default:
            result =
                MessageConfig.INTERFACE_CALL_ERROR_MSG()
                    + ":"
                    + EntityUtils.toString(response.getEntity(), "utf-8");
            logger.warn("call failed,return msg:" + result);
            break;
        }
        // Release link
        response.close();
      } catch (ClientProtocolException e) {
        logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
        result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ClientProtocolException");
      } catch (ParseException e) {
        logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
        result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":ParseException");
      } catch (IOException e) {
        logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
        result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":IOException");
      } catch (URISyntaxException e) {
        logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
        result = (MessageConfig.INTERFACE_CALL_ERROR_MSG() + ":URISyntaxException");
      }
    }
    return result;
  }

  /**
   * Get request to back response
   *
   * @param url url
   * @param map Request incoming parameters ("key" is the parameter name, "value" is the parameter
   *     value) "map" can be empty
   * @param timeOutMS (Millisecond)
   */
  public static CloseableHttpResponse doGetReturnResponse(
      String url, Map<String, String> map, Integer timeOutMS) {
    CloseableHttpResponse response = null;
    if (StringUtils.isNotBlank(url)) {
      try {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet;
        // Determine whether to add parameters
        if (null != map && !map.isEmpty()) {
          // Since the parameters of the "GET" request are all assembled behind the "URL" address,
          // we have to build a "URL" with parameters.
          URIBuilder uriBuilder = new URIBuilder(url);

          List<NameValuePair> list = new LinkedList<>();
          for (String key : map.keySet()) {
            BasicNameValuePair param = new BasicNameValuePair(key, map.get(key));
            list.add(param);
          }

          uriBuilder.setParameters(list);
          // Construct a "GET" request object from a "URI" object with parameters
          httpGet = new HttpGet(uriBuilder.build());
        } else {
          httpGet = new HttpGet(url);
        }
        // Type of transmission
        httpGet.addHeader("Content-type", "application/json");
        if (null != timeOutMS) {
          // Set timeout
          RequestConfig requestConfig =
              RequestConfig.custom()
                  .setConnectTimeout(5000)
                  .setConnectionRequestTimeout(1000)
                  .setSocketTimeout(timeOutMS)
                  .build();
          httpGet.setConfig(requestConfig);
        }

        logger.info("call '" + url + "' start");
        // Get the response object by requesting the object
        response = httpClient.execute(httpGet);
        // Get result entity
        // Determine whether the network connection status code is normal (0--200 are normal)
        // Release link
      } catch (Exception e) {
        logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
      }
    }
    return response;
  }
}
