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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * anonymous complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CscdServiceException" type="{http://webservice.cscd.com}Exception" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cscdServiceException"})
@XmlRootElement(name = "CscdServiceException")
public class CscdServiceException {

    @XmlElementRef(name = "CscdServiceException", namespace = "http://webservice.cscd.com", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Exception> cscdServiceException;

    /**
     * 获取cscdServiceException属性的值。
     *
     * @return possible object is {@link JAXBElement }{@code <}{@link java.lang.Exception }{@code >}
     */
    public JAXBElement<java.lang.Exception> getCscdServiceException() {
        return cscdServiceException;
    }

    /**
     * 设置cscdServiceException属性的值。
     *
     * @param value allowed object is {@link JAXBElement }{@code <}{@link java.lang.Exception }{@code
     *     >}
     */
    public void setCscdServiceException(JAXBElement<java.lang.Exception> value) {
        this.cscdServiceException = value;
    }
}
