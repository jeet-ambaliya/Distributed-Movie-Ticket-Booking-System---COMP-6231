package Replicas.Replica1.implementation;

import Replicas.Replica1.MTBInterface.MTBSInterface;
import Replicas.Replica1.model.BookingDetails;
import config.CONFIGURATION;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebService(endpointInterface = "Replicas.Replica1.MTBInterface.MTBSInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class VERImplementation implements MTBSInterface {


    Map<String, Map<String, BookingDetails>> VERdata = null;
    public Map<String, BookingDetails> userSlotData = null;
    public List<String> customerList = null;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    FileHandler fh;
    SimpleFormatter formatter = new SimpleFormatter();
    public String out_result = "", atw_result = "";
    TimeUnit time = TimeUnit.SECONDS;

    boolean check = false;
    boolean newCheck = false;


    public VERImplementation() {
        super();

        VERdata = new ConcurrentHashMap<>();
        userSlotData = new ConcurrentHashMap<>();
        customerList = new ArrayList<>();

    }

    @Override
    public String addMovieSlots(String movieID, String movieName, int bookingCapacity) {

        String result = "";

        if (movieID.charAt(3) == 'M' | movieID.charAt(3) == 'A' | movieID.charAt(3) == 'E') {
            if (movieID.startsWith(CONFIGURATION.VERSERVER)) {
                try {
                    if (!checkDate(movieID)) {
                        result = "Failed";
                        writeLog("Tickets cannot be booked for Date more than a week or for previous date.");

                    } else {
                        if (!(movieID.startsWith(CONFIGURATION.VERSERVER))) {
                            result = "Failed";
                            writeLog("Unable to add slot : Invalid movieID");
                        } else if (!VERdata.containsKey(movieName)) {
                            Map<String, BookingDetails> tmp = new ConcurrentHashMap<>();
                            tmp.put(movieID, new BookingDetails(new ArrayList<>(), bookingCapacity));
                            VERdata.put(movieName, tmp);
                            result = "Success";
                            writeLog("Movie slot " + movieID + " added for : " + movieName);
                        } else {
                            if (VERdata.get(movieName).containsKey(movieID)) {
                                result = "Failed";
                                writeLog("Movie Slot already exist for " + movieID);
                            } else {
                                Map<String, BookingDetails> tmp = VERdata.get(movieName);
                                tmp.put(movieID, new BookingDetails(new ArrayList<>(), bookingCapacity));
                                VERdata.put(movieName, tmp);
                                result = "Success";
                                writeLog("Movie slot " + movieID + " added for : " + movieName);

                            }
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                result = "Failed";
                writeLog("Unable to add slots for other servers.");
            }
        } else {
            result = "Failed";
            writeLog("Unable to add slot : Invalid movieID");

        }

        return result;
    }

    @Override
    public String removeMovieSlots(String movieID, String movieName) {
        String result = "";
        boolean transfer = false;
        String log = "";
        int day = Integer.parseInt(movieID.substring(4, 6));
        int month = Integer.parseInt(movieID.substring(6, 8));

        if (!VERdata.containsKey(movieName)) {
            result = "Failed";
            log = "No movie slots is available for this type!!!";
        } else {
            Map<String, BookingDetails> tmp = VERdata.get(movieName);
            if (tmp.containsKey(movieID)) {
                if (tmp.get(movieID).getCustomerID().size() == 0) {
                    result = "Failed";
                    VERdata.get(movieName).remove(movieID);
                    log = "Movie show found, no shows booked, deleting it without transfer";
                } else {
                    result = "Failed";
                    log = "Movie slot found, customers have booked this shows, unable to find other movie shows for the customers.";
                    for (Map.Entry<String, BookingDetails> bookingData : tmp.entrySet()) {
                        if ((Integer.parseInt(bookingData.getKey().substring(4, 8)) > month)
                                || (Integer.parseInt(bookingData.getKey().substring(4, 6)) > day)) {
                            if ((bookingData.getValue().getCapacity() - bookingData.getValue().getCustomerID().size()) >= tmp
                                    .get(movieID).getCustomerID().size()) {
                                transfer = true;
                                List<String> customers = new ArrayList<>();
                                customers.addAll(bookingData.getValue().getCustomerID());
                                customers.addAll(tmp.get(movieID).getCustomerID());

                                VERdata.get(movieName).get(bookingData.getKey()).setCustomerID(customers);
                                VERdata.get(movieName).remove(movieID);
                                result = "Success";
                                log = "Slot is deleted and booking is now transferred to show " + bookingData.getKey();

                                break;
                            }
                        }
                    }
                    if (transfer) {
                        result = "Success";
                        log = "Slot deleted, transferred booking to new show.";
                    } else {
                        result = "Failed";
                        log = "Booking found for this show, Unable to assign to other shows.";

                        String customers = "";
                        for (String temp : VERdata.get(movieName).get(movieID).getCustomerID()) {
                            customers += temp + ",";
                        }


                        String finalCustomers = customers;

                        atw_result = udpThread("removeSlots:" + finalCustomers + " " + movieName, CONFIGURATION.ATW_LISTENER);

                        if (atw_result.contains("success")) {
                            VERdata.get(movieName).remove(movieID);
                            result = "Success";
                            log = "Slot is deleted and booking is now transferred to Verdun show: " + atw_result.split(" ")[1];

                        } else {
                            out_result = udpThread("removeSlots:" + finalCustomers + " " + movieName, CONFIGURATION.OUT_LISTENER);
                            if (out_result.contains("success")) {
                                VERdata.get(movieName).remove(movieID);
                                result = "Success";
                                log = "Slot is deleted and booking is now transferred to Outremont show: " + out_result.split(" ")[1];
                            }
                        }
                    }
                }

            } else {
                result = "Failed";
                VERdata.get(movieName).remove(movieID);
                log = "No shows are available for " + movieName + "!!!";
            }
        }

        writeLog(log);

        return result;
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        String result = "";
        String log = "";
        if (VERdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : VERdata.get(movieName).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() >= 0))
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + ",");

            }
        }

        if (result.contains("VERA") | result.contains("VERM") | result.contains("VERE"))
            result += "\n";
        else
            result += "";

        new Thread() {
            public void run() {
                out_result = udpThread("showsList:" + movieName, CONFIGURATION.OUT_LISTENER);
            }
        }.start();

        new Thread() {
            public void run() {
                atw_result = udpThread("showsList:" + movieName, CONFIGURATION.ATW_LISTENER);
            }
        }.start();

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String final_result = result + out_result + atw_result;
        log = final_result.trim().isEmpty() ? "No result Found!!" : "Data found from all servers";
        writeLog(log);
        return (final_result.trim().isEmpty() ? "No result Found!!" : final_result);
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        String result = "";
        String status = "failed";
        int emptySlots = 0;
        String ID = "";
        String log = "";

        List<String> slots = Arrays.asList(retriveAvailableSlots(movieName).split(","));

        if (movieID.startsWith(CONFIGURATION.VERSERVER)) {
            if (customerID.startsWith(CONFIGURATION.ATWSERVER) | customerID.startsWith(CONFIGURATION.OUTSERVER)) {
                boolean isAvailable = true;
                String date = movieID.substring(3, 10);
                for (int i = 0; i < slots.size(); i++) {
                    if (slots.get(i).substring(3, 10).equals(date))
                        isAvailable = false;
                }
                if (!isAvailable) {
                    result = "Failed";
                    log = "Unable to book show with same timing more than once";

                }
            } else {
                if (VERdata.containsKey(movieName) && VERdata.get(movieName).containsKey(movieID)) {
                    for (int i = 0; i < slots.size(); i++) {
                        ID = slots.get(i).split(":")[0].trim();
                        emptySlots = Integer.parseInt(slots.get(i).split(":")[1].trim());
                        if (emptySlots >= numberOfTickets && ID.contains(movieID)) {
                            int numberOfSeats = (VERdata.get(movieName).get(movieID).getCapacity()
                                    - VERdata.get(movieName).get(movieID).getCustomerID().size());
                            if (numberOfSeats >= numberOfTickets) {
                                BookingDetails bookings = VERdata.get(movieName).get(movieID);
                                for (int j = 0; j < numberOfTickets; j++) {
                                    bookings.getCustomerID().add(customerID);

                                }
                                result = "Success";
                                log = "Tickets for " + movieName + " for " + movieID.substring(4, 6)
                                        + "/" + movieID.substring(6, 8);
                                break;
                            } else {
                                System.out.println("Slot not Found");
                                result = "Failed";
                                log = "Not enough seats available for this show!!!";

                            }
                        } else {
                            result = "Failed";
                        }
                    }
                } else {
                    result = "Failed";
                    log = "Unable to book show " + movieID + " for " + movieName;
                }
            }
        } else {
            int totalBookings = getTotalBookings(customerID);

            if (totalBookings <= 2) {
                if (movieID.contains(CONFIGURATION.OUTSERVER)) {
                    result = udpThread("bookTickets:" + customerID + " " + movieID + " " + movieName + " " + numberOfTickets, CONFIGURATION.OUT_LISTENER);

                } else if (movieID.contains(CONFIGURATION.ATWSERVER)) {
                    result = udpThread("bookTickets:" + customerID + " " + movieID + " " + movieName + " " + numberOfTickets, CONFIGURATION.ATW_LISTENER);
                } else {
                    result = "Failed";
                    log = "Failed : Invalid movieID";
                }

            } else {
                result = "Failed";
                log = "Failed : Unable to book more than 3 tickets for Different Locations";
            }
        }

        writeLog(log);
        return result;
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String key = null;
        String log = "";
        String result = "";

        for (Map.Entry<String, Map<String, BookingDetails>> set : VERdata.entrySet()) {
            key = set.getKey();
            boolean isAppended = false;
            int count = 0;
            for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {

                for (int i = 0; i < data.getValue().getCustomerID().size(); i++) {
                    if (data.getValue().getCustomerID().get(i).equals(customerID))
                        count++;
                }
                if ((data.getValue().getCustomerID()).contains(customerID)) {
                    result += "Movie Name: " + key + " | Show ID: " + data.getKey() + " | Tickets Booked: " + count + "\n";
                    count = 0;
                }

            }
        }

        if (result.contains("VERA") | result.contains("VERM") | result.contains("VERE"))
            result += "";
        else
            result = "";

        new Thread() {
            @Override
            public void run() {
                atw_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.ATW_LISTENER);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                out_result = "\n" + udpThread("showsSchedule:" + customerID, CONFIGURATION.OUT_LISTENER);
            }
        }.start();

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (result.equals(""))
            result = "No Bookings found\n";

        if (atw_result.equals(""))
            atw_result = "No Bookings found\n";

        if (out_result.equals(""))
            out_result = "No Bookings found\n";


        String final_result = "----------Atwater----------\n" + atw_result + "----------Verdun----------\n" + result + "----------Outremont----------\n" + out_result;


        log = final_result.trim().isEmpty() ? "No result Found!!" : "Bookings found from all servers for user : " + customerID;
        writeLog(log);


        return (result.isEmpty() ? "" : final_result);
    }

    public String getBookingScheduleForServer(String customerID) {
        String key = null;
        String log = "";
        String result = "";

        for (Map.Entry<String, Map<String, BookingDetails>> set : VERdata.entrySet()) {
            key = set.getKey();
            int count = 0;
            for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {

                for (int i = 0; i < data.getValue().getCustomerID().size(); i++) {
                    if (data.getValue().getCustomerID().get(i).equals(customerID))
                        count++;
                }
                if ((data.getValue().getCustomerID()).contains(customerID)) {
                    result += "Movie Name: " + key + " | Show ID: " + data.getKey() + " | Tickets Booked: " + count + "\n";
                    count = 0;
                }

            }
        }

        if (result.contains("VERA") | result.contains("VERM") | result.contains("VERE"))
            result += "\n";
        else
            result = "";

        log = result.trim().isEmpty() ? "No result Found!!" : "Bookings found for Verdun server for user " + customerID;
        writeLog(log);

        return result;
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) {
        String status = "failed";
        String log = "No Bookings found\n";
        String result = "Failed";


        String server = movieId.substring(0, 3);

        String serverData = customerID + " " + movieId + " " + movieName + " " + numberOfTickets;

        switch (server) {
            case "VER":
                for (Map.Entry<String, Map<String, BookingDetails>> set : VERdata.entrySet()) {
                    for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {
                        if (data.getValue().getCustomerID().stream().filter(customerID::equals).count() >= numberOfTickets) {
                            if (data.getValue().getCustomerID().contains(customerID) && data.getKey().equals(movieId)) {
                                System.out.println("Bookings Found");
                                for (int i = 0; i < numberOfTickets; i++) {
                                    data.getValue().getCustomerID().remove(customerID);
                                }
                                log = "Ticket(s) canceled successfully.";
                                status = "success";
                                result = "Ticket(s) canceled!!!";
                                break;
                            }
                        }
                    }
                }
                break;
            case "ATW":
                new Thread() {
                    public void run() {
                        atw_result = udpThread("cancelTickets:" + serverData, CONFIGURATION.ATW_LISTENER);
                    }
                }.start();
                result = atw_result;
                break;
            case "OUT":
                result = "";
                new Thread() {
                    public void run() {
                        out_result = udpThread("cancelTickets:" + serverData, CONFIGURATION.OUT_LISTENER);
                    }
                }.start();
                result = out_result;
                break;
        }
        writeLog(log);

        return result;
    }

    @Override
    public String exchangeTickets(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {
        if (movieID.substring(0, 3).equals(CONFIGURATION.VERSERVER)) {

            if (VERdata.containsKey(old_movieName)) {
                if (VERdata.get(old_movieName).containsKey(movieID)) {
                    if (VERdata.get(old_movieName).get(movieID).getCustomerID().contains(customerID)) {

                        List<String> s = VERdata.get(old_movieName).get(movieID).getCustomerID();
                        int count = 0;
                        for (String id : s) {
                            if (id.equals(customerID))
                                count++;
                        }

                        int size = VERdata.get(old_movieName).get(movieID).getCapacity();

                        if (count >= numberOfTickets && size >= numberOfTickets)
                            check = true;

                    }
                }
            }
        } else if (movieID.substring(0, 3).equals(CONFIGURATION.OUTSERVER)) {
            new Thread() {
                public void run() {
                    out_result = udpThread("checkMovieTicket:" + customerID + " " + old_movieName + " " + movieID + " " + new_movieID + " " + new_movieName + " " + numberOfTickets, CONFIGURATION.OUT_LISTENER);
                    if (out_result.equals("done")) {
                        check = true;
                    }

                }
            }.start();
            try {
                time.sleep(2L);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } else if (movieID.substring(0, 3).equals(CONFIGURATION.ATWSERVER)) {
            new Thread() {
                public void run() {
                    atw_result = udpThread("checkMovieTicket:" + customerID + " " + old_movieName + " " + movieID + " " + new_movieID + " " + new_movieName + " " + numberOfTickets, CONFIGURATION.ATW_LISTENER);
                    if (atw_result.equals("done")) {
                        check = true;
                    }

                }
            }.start();
            try {
                time.sleep(2L);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }


//        For new movie
        if (new_movieID.substring(0, 3).equals(CONFIGURATION.VERSERVER)) {

            if (VERdata.containsKey(new_movieName)) {
                if (VERdata.get(new_movieName).containsKey(new_movieID)) {
                    int numberOfSeats = (VERdata.get(new_movieName).get(movieID).getCapacity()
                            - VERdata.get(new_movieName).get(movieID).getCustomerID().size());

                    if (numberOfSeats >= numberOfTickets)
                        newCheck = true;
                }
            }
        } else if (new_movieID.substring(0, 3).equals(CONFIGURATION.OUTSERVER)) {
            new Thread() {
                public void run() {
                    out_result = udpThread("checkNewMovieTicket:" + customerID + " " + old_movieName + " " + movieID + " " + new_movieID + " " + new_movieName + " " + numberOfTickets, CONFIGURATION.OUT_LISTENER);
                    if (out_result.equals("done")) {
                        newCheck = true;
                    }

                }
            }.start();
            try {
                time.sleep(2L);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } else if (new_movieID.substring(0, 3).equals(CONFIGURATION.ATWSERVER)) {
            new Thread() {
                public void run() {
                    atw_result = udpThread("checkNewMovieTicket:" + customerID + " " + old_movieName + " " + movieID + " " + new_movieID + " " + new_movieName + " " + numberOfTickets, CONFIGURATION.ATW_LISTENER);
                    if (atw_result.equals("done")) {
                        newCheck = true;
                    }

                }
            }.start();
            try {
                time.sleep(2);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        if (check && newCheck) {
            System.out.println("Exchange is possible");
            String s1 = cancelMovieTickets(customerID, movieID, old_movieName, numberOfTickets);
            String s2 = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);
            return "Success";

        } else {
            return "Failed";
        }
    }


    private int getTotalBookings(String customerID) {
        List<String> slots;
        slots = Arrays.asList(retriveCustomerBookingsFromServers(customerID, CONFIGURATION.ATWSERVER).split(","));
        int totalBookings = 0;
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).contains(CONFIGURATION.ATWSERVER)) {
                totalBookings += Integer.parseInt(slots.get(i).split(":")[1].trim());
            }
        }

        slots = Arrays.asList(retriveCustomerBookingsFromServers(customerID, CONFIGURATION.OUTSERVER).split(","));
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).contains(CONFIGURATION.OUTSERVER)) {
                totalBookings += Integer.parseInt(slots.get(i).split(":")[1].trim());
            }
        }
        return totalBookings;
    }

    public String retriveAvailableSlots(String movieName) {
        String key = null;
        String result = "";

        if (VERdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : VERdata.get(movieName).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() >= 0))
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + ",");

            }
        }


        return (result.isEmpty() ? "" : result);
    }

    public String retriveCustomerBookingsFromServers(String customerID, String server) {
        String key = null;
        String result = "";

        if (server.equals(CONFIGURATION.ATWSERVER)) {
            new Thread() {
                @Override
                public void run() {
                    atw_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.ATW_LISTENER);
                    atw_result = atw_result.replace("----------Atwater----------", "Atwater");

                }
            }.start();
            return (atw_result.isEmpty() ? "" : atw_result);
        } else if (server.equals(CONFIGURATION.OUTSERVER)) {
            new Thread() {
                @Override
                public void run() {
                    out_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.OUT_LISTENER);
                    out_result = out_result.replace("----------Outremont----------", "Outremont");
                }
            }.start();
            return (out_result.isEmpty() ? "" : out_result);

        }

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String final_result = result + out_result + atw_result;

        return (result.isEmpty() ? "" : final_result);
    }

    public String listAvailableForServer(String movieName) {
        String result = "";
        if (VERdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : VERdata.get(movieName.trim()).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() > 0)) {
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + "\n");
                }
            }
        }
        return result;
    }

    public String udpThread(String data, int port) {
        String result = "";
        try (DatagramSocket aSocket = new DatagramSocket()) {
            DatagramPacket request = new DatagramPacket(data.getBytes(), data.getBytes().length,
                    InetAddress.getByName("localhost"), port);
            aSocket.send(request);
            writeLog("UDP request sent from Verdun to port " + port);


            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            aSocket.close();
            result = new String(reply.getData()).trim();
            writeLog("UDP response received from port " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String ServerexchangeTicketsCheck(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {

        if (VERdata.containsKey(old_movieName)) {
            if (VERdata.get(old_movieName).containsKey(movieID)) {
                if (VERdata.get(old_movieName).get(movieID).getCustomerID().contains(customerID)) {

                    List<String> s = VERdata.get(old_movieName).get(movieID).getCustomerID();
                    int count = 0;
                    for (String id : s) {
                        if (id.equals(customerID))
                            count++;
                    }

                    if (count >= numberOfTickets)
                        return "done";

                }
            }
        }

        return "not done";
    }

    public String ServerexchangeTicketsCheckNewMovie(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {
        if (VERdata.containsKey(new_movieName)) {
            if (VERdata.get(new_movieName).containsKey(new_movieID)) {
                int numberOfSeats = (VERdata.get(new_movieName).get(movieID).getCapacity()
                        - VERdata.get(new_movieName).get(movieID).getCustomerID().size());

                System.out.println("Reached inside");
                if (numberOfSeats >= numberOfTickets)
                    return "done";
            }
        }
        System.out.println("Reached outside");

        return "not done";
    }

    public boolean checkDate(String movieID) throws ParseException {

        String date_temp = movieID.substring(4);
        Date date = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);


        String tempCurrentDate = new SimpleDateFormat("ddMMyy").format(new Date());
        Date currentDate = new SimpleDateFormat("ddMMyy").parse(tempCurrentDate);
        ;
        Date nextWeek = null;
        String temp = new SimpleDateFormat("ddMMyy").format(c.getTime());

        nextWeek = new SimpleDateFormat("ddMMyy").parse(temp);
        date = new SimpleDateFormat("ddMMyy").parse(date_temp);

        if (date.equals(currentDate)) {
            return true;
        } else if (date.before(currentDate)) {
            return false;
        } else if (date.after(nextWeek)) {
            return false;
        }

        return true;

    }

    public void writeLog(String message) {
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("src/logs/VERLog.log", 0, 1, true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            // the following statement is used to log any messages
            logger.info("Log from Verdun : " + message);

            fh.close();

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }


}