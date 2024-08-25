/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cscd.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the com.cscd.webservice package.
 *
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema
 * derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _SearchByExprCode_QNAME =
        new QName("http://webservice.cscd.com", "code");
    private static final QName _SearchByExprExpr_QNAME =
        new QName("http://webservice.cscd.com", "expr");
    private static final QName _SearchByExprResponseReturn_QNAME =
        new QName("http://webservice.cscd.com", "return");
    private static final QName _CscdServiceExceptionCscdServiceException_QNAME =
        new QName("http://webservice.cscd.com", "CscdServiceException");
    private static final QName _GetArticlesCscdIds_QNAME =
        new QName("http://webservice.cscd.com", "cscdIds");
    private static final QName _GetCitedInfoCscdId_QNAME =
        new QName("http://webservice.cscd.com", "cscdId");
    private static final QName _SearchArticlesAuthor_QNAME =
        new QName("http://webservice.cscd.com", "author");
    private static final QName _SearchArticlesInstitute_QNAME =
        new QName("http://webservice.cscd.com", "institute");
    private static final QName _SearchArticlesTitle_QNAME =
        new QName("http://webservice.cscd.com", "title");
    private static final QName _SearchArticlesOrcId_QNAME =
        new QName("http://webservice.cscd.com", "orcId");
    private static final QName _GetCodeUser_QNAME = new QName("http://webservice.cscd.com", "user");
    private static final QName _GetCodePasswd_QNAME =
        new QName("http://webservice.cscd.com", "passwd");
    private static final QName _ExceptionMessage_QNAME =
        new QName("http://webservice.cscd.com", "Message");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes
     * for package: com.cscd.webservice
     */
    public ObjectFactory() {
    }

    /** Create an instance of {@link SearchByExpr } */
    public SearchByExpr createSearchByExpr() {
        return new SearchByExpr();
    }

    /** Create an instance of {@link SearchByExprResponse } */
    public SearchByExprResponse createSearchByExprResponse() {
        return new SearchByExprResponse();
    }

    /** Create an instance of {@link SearchByExprRange } */
    public SearchByExprRange createSearchByExprRange() {
        return new SearchByExprRange();
    }

    /** Create an instance of {@link SearchByExprRangeResponse } */
    public SearchByExprRangeResponse createSearchByExprRangeResponse() {
        return new SearchByExprRangeResponse();
    }

    /** Create an instance of {@link ReleaseCode } */
    public ReleaseCode createReleaseCode() {
        return new ReleaseCode();
    }

    /** Create an instance of {@link CscdServiceException } */
    public CscdServiceException createCscdServiceException() {
        return new CscdServiceException();
    }

    /** Create an instance of {@link Exception } */
    public java.lang.Exception createException() {
        return new java.lang.Exception();
    }

    /** Create an instance of {@link GetArticles } */
    public GetArticles createGetArticles() {
        return new GetArticles();
    }

    /** Create an instance of {@link GetArticlesResponse } */
    public GetArticlesResponse createGetArticlesResponse() {
        return new GetArticlesResponse();
    }

    /** Create an instance of {@link GetCitedInfo } */
    public GetCitedInfo createGetCitedInfo() {
        return new GetCitedInfo();
    }

    /** Create an instance of {@link GetCitedInfoResponse } */
    public GetCitedInfoResponse createGetCitedInfoResponse() {
        return new GetCitedInfoResponse();
    }

    /** Create an instance of {@link SearchArticles } */
    public SearchArticles createSearchArticles() {
        return new SearchArticles();
    }

    /** Create an instance of {@link SearchArticlesResponse } */
    public SearchArticlesResponse createSearchArticlesResponse() {
        return new SearchArticlesResponse();
    }

    /** Create an instance of {@link GetCode } */
    public GetCode createGetCode() {
        return new GetCode();
    }

    /** Create an instance of {@link GetCodeResponse } */
    public GetCodeResponse createGetCodeResponse() {
        return new GetCodeResponse();
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = SearchByExpr.class)
    public JAXBElement<String> createSearchByExprCode(String value) {
        return new JAXBElement<String>(
            _SearchByExprCode_QNAME, String.class, SearchByExpr.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "expr", scope = SearchByExpr.class)
    public JAXBElement<String> createSearchByExprExpr(String value) {
        return new JAXBElement<String>(
            _SearchByExprExpr_QNAME, String.class, SearchByExpr.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = SearchByExprResponse.class)
    public JAXBElement<String> createSearchByExprResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, SearchByExprResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = SearchByExprRange.class)
    public JAXBElement<String> createSearchByExprRangeCode(String value) {
        return new JAXBElement<String>(
            _SearchByExprCode_QNAME, String.class, SearchByExprRange.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "expr", scope = SearchByExprRange.class)
    public JAXBElement<String> createSearchByExprRangeExpr(String value) {
        return new JAXBElement<String>(
            _SearchByExprExpr_QNAME, String.class, SearchByExprRange.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = SearchByExprRangeResponse.class)
    public JAXBElement<String> createSearchByExprRangeResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, SearchByExprRangeResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = ReleaseCode.class)
    public JAXBElement<String> createReleaseCodeCode(String value) {
        return new JAXBElement<String>(_SearchByExprCode_QNAME, String.class, ReleaseCode.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "CscdServiceException", scope = CscdServiceException.class)
    public JAXBElement<java.lang.Exception> createCscdServiceExceptionCscdServiceException(
                                                                                           java.lang.Exception value) {
        return new JAXBElement<java.lang.Exception>(
            _CscdServiceExceptionCscdServiceException_QNAME,
            java.lang.Exception.class,
            CscdServiceException.class,
            value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = GetArticles.class)
    public JAXBElement<String> createGetArticlesCode(String value) {
        return new JAXBElement<String>(_SearchByExprCode_QNAME, String.class, GetArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "cscdIds", scope = GetArticles.class)
    public JAXBElement<String> createGetArticlesCscdIds(String value) {
        return new JAXBElement<String>(
            _GetArticlesCscdIds_QNAME, String.class, GetArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = GetArticlesResponse.class)
    public JAXBElement<String> createGetArticlesResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, GetArticlesResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = GetCitedInfo.class)
    public JAXBElement<String> createGetCitedInfoCode(String value) {
        return new JAXBElement<String>(
            _SearchByExprCode_QNAME, String.class, GetCitedInfo.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "cscdId", scope = GetCitedInfo.class)
    public JAXBElement<String> createGetCitedInfoCscdId(String value) {
        return new JAXBElement<String>(
            _GetCitedInfoCscdId_QNAME, String.class, GetCitedInfo.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = GetCitedInfoResponse.class)
    public JAXBElement<String> createGetCitedInfoResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, GetCitedInfoResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "code", scope = SearchArticles.class)
    public JAXBElement<String> createSearchArticlesCode(String value) {
        return new JAXBElement<String>(
            _SearchByExprCode_QNAME, String.class, SearchArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "author", scope = SearchArticles.class)
    public JAXBElement<String> createSearchArticlesAuthor(String value) {
        return new JAXBElement<String>(
            _SearchArticlesAuthor_QNAME, String.class, SearchArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "institute", scope = SearchArticles.class)
    public JAXBElement<String> createSearchArticlesInstitute(String value) {
        return new JAXBElement<String>(
            _SearchArticlesInstitute_QNAME, String.class, SearchArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "title", scope = SearchArticles.class)
    public JAXBElement<String> createSearchArticlesTitle(String value) {
        return new JAXBElement<String>(
            _SearchArticlesTitle_QNAME, String.class, SearchArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "orcId", scope = SearchArticles.class)
    public JAXBElement<String> createSearchArticlesOrcId(String value) {
        return new JAXBElement<String>(
            _SearchArticlesOrcId_QNAME, String.class, SearchArticles.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = SearchArticlesResponse.class)
    public JAXBElement<String> createSearchArticlesResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, SearchArticlesResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "user", scope = GetCode.class)
    public JAXBElement<String> createGetCodeUser(String value) {
        return new JAXBElement<String>(_GetCodeUser_QNAME, String.class, GetCode.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "passwd", scope = GetCode.class)
    public JAXBElement<String> createGetCodePasswd(String value) {
        return new JAXBElement<String>(_GetCodePasswd_QNAME, String.class, GetCode.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "return", scope = GetCodeResponse.class)
    public JAXBElement<String> createGetCodeResponseReturn(String value) {
        return new JAXBElement<String>(
            _SearchByExprResponseReturn_QNAME, String.class, GetCodeResponse.class, value);
    }

    /** Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}} */
    @XmlElementDecl(namespace = "http://webservice.cscd.com", name = "Message", scope = java.lang.Exception.class)
    public JAXBElement<String> createExceptionMessage(String value) {
        return new JAXBElement<String>(
            _ExceptionMessage_QNAME, String.class, java.lang.Exception.class, value);
    }
}
