
package Replicas.Replica2.com.example.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "OUTServerImpl", targetNamespace = "http://example.com/out")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface OUTServerImpl {


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
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/addMovieSlotsRequest", output = "http://example.com/out/OUTServerImpl/addMovieSlotsResponse")
    public String addMovieSlots(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
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
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/cancelMovieTicketsRequest", output = "http://example.com/out/OUTServerImpl/cancelMovieTicketsResponse")
    public String cancelMovieTickets(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3);

    /**
     * 
     * @param arg5
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/exchangeTicketsRequest", output = "http://example.com/out/OUTServerImpl/exchangeTicketsResponse")
    public String exchangeTickets(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        String arg3,
        @WebParam(name = "arg4", partName = "arg4")
        String arg4,
        @WebParam(name = "arg5", partName = "arg5")
        int arg5);

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
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/bookMovieTicketsRequest", output = "http://example.com/out/OUTServerImpl/bookMovieTicketsResponse")
    public String bookMovieTickets(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3);

    /**
     * 
     */
    @WebMethod
    @Action(input = "http://example.com/out/OUTServerImpl/createVERObjectRequest", output = "http://example.com/out/OUTServerImpl/createVERObjectResponse")
    public void createVERObject();

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/removeMovieSlotsRequest", output = "http://example.com/out/OUTServerImpl/removeMovieSlotsResponse")
    public String removeMovieSlots(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2);

    /**
     * 
     */
    @WebMethod
    @Action(input = "http://example.com/out/OUTServerImpl/createOUTObjectRequest", output = "http://example.com/out/OUTServerImpl/createOUTObjectResponse")
    public void createOUTObject();

    /**
     * 
     */
    @WebMethod
    @Action(input = "http://example.com/out/OUTServerImpl/createATWObjectRequest", output = "http://example.com/out/OUTServerImpl/createATWObjectResponse")
    public void createATWObject();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/getBookingScheduleRequest", output = "http://example.com/out/OUTServerImpl/getBookingScheduleResponse")
    public String getBookingSchedule(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://example.com/out/OUTServerImpl/listMovieShowsAvailabilityRequest", output = "http://example.com/out/OUTServerImpl/listMovieShowsAvailabilityResponse")
    public String listMovieShowsAvailability(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

}
