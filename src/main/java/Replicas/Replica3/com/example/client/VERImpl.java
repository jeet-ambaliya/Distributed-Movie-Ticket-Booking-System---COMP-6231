
package com.example.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "VERImpl", targetNamespace = "http://example.com/ver")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface VERImpl {


    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "removeMovieSlotsVER", targetNamespace = "http://example.com/ver", className = "com.example.client.RemoveMovieSlotsVER")
    @ResponseWrapper(localName = "removeMovieSlotsVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.RemoveMovieSlotsVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/removeMovieSlotsVERRequest", output = "http://example.com/ver/VERImpl/removeMovieSlotsVERResponse")
    public String removeMovieSlotsVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listMovieShowsAvailabilityVER", targetNamespace = "http://example.com/ver", className = "com.example.client.ListMovieShowsAvailabilityVER")
    @ResponseWrapper(localName = "listMovieShowsAvailabilityVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.ListMovieShowsAvailabilityVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/listMovieShowsAvailabilityVERRequest", output = "http://example.com/ver/VERImpl/listMovieShowsAvailabilityVERResponse")
    public String listMovieShowsAvailabilityVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "addMovieSlotsVER", targetNamespace = "http://example.com/ver", className = "com.example.client.AddMovieSlotsVER")
    @ResponseWrapper(localName = "addMovieSlotsVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.AddMovieSlotsVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/addMovieSlotsVERRequest", output = "http://example.com/ver/VERImpl/addMovieSlotsVERResponse")
    public String addMovieSlotsVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        int arg3);

    /**
     * 
     * @param arg5
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    @RequestWrapper(localName = "logVER", targetNamespace = "http://example.com/ver", className = "com.example.client.LogVER")
    @ResponseWrapper(localName = "logVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.LogVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/logVERRequest", output = "http://example.com/ver/VERImpl/logVERResponse")
    public void logVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        String arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        String arg5);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBookingScheduleVER", targetNamespace = "http://example.com/ver", className = "com.example.client.GetBookingScheduleVER")
    @ResponseWrapper(localName = "getBookingScheduleVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.GetBookingScheduleVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/getBookingScheduleVERRequest", output = "http://example.com/ver/VERImpl/getBookingScheduleVERResponse")
    public String getBookingScheduleVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "bookMovieTicketsVER", targetNamespace = "http://example.com/ver", className = "com.example.client.BookMovieTicketsVER")
    @ResponseWrapper(localName = "bookMovieTicketsVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.BookMovieTicketsVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/bookMovieTicketsVERRequest", output = "http://example.com/ver/VERImpl/bookMovieTicketsVERResponse")
    public String bookMovieTicketsVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        int arg3);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "cancelMovieTicketsVER", targetNamespace = "http://example.com/ver", className = "com.example.client.CancelMovieTicketsVER")
    @ResponseWrapper(localName = "cancelMovieTicketsVERResponse", targetNamespace = "http://example.com/ver", className = "com.example.client.CancelMovieTicketsVERResponse")
    @Action(input = "http://example.com/ver/VERImpl/cancelMovieTicketsVERRequest", output = "http://example.com/ver/VERImpl/cancelMovieTicketsVERResponse")
    public String cancelMovieTicketsVER(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        int arg3);

}
