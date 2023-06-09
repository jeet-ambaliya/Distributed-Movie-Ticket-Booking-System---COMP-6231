package Replicas.Replica2.com.example.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Replicas.Replica2.com.example.logging.LoggingHelper;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://example.com/atw")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ATWServerImpl implements BookingSystemInterface {


    private HashMap<String, HashMap<String, Integer>> movies = new HashMap<>();

    private HashMap<String, List<String[]>> customerBookings = new HashMap<>();


    /**
     * Adds the movie slots for ADMIN
     *
     * @param adminID         The admin id who is sending the request.
     * @param movieID         The ID of the movie.
     * @param movieName       The name of the movie.
     * @param bookingCapacity The capacity of the theatre.
     */
    @Override
    public String addMovieSlots(String adminID, String movieID, String movieName, int bookingCapacity) {
        boolean checkValidDate;
        String date_temp = movieID.substring(4);
        Date date1 = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, 7);

        try {
            String tempCurrentDate = new SimpleDateFormat("ddMMyy").format(new Date());
            Date currentDate = null;

            currentDate = new SimpleDateFormat("ddMMyy").parse(tempCurrentDate);

            Date nextWeek = null;
            String temp = new SimpleDateFormat("ddMMyy").format(c.getTime());

            nextWeek = new SimpleDateFormat("ddMMyy").parse(temp);
            date1 = new SimpleDateFormat("ddMMyy").parse(date_temp);

            if (date1.equals(currentDate)) {
                checkValidDate = true;
            } else if (date1.before(currentDate)) {
                checkValidDate = false;
            } else if (date1.after(nextWeek)) {
                checkValidDate = false;
            } else {
                checkValidDate = true;
            }

            if (!checkValidDate) {
                return "Failed";
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String requestParameters = adminID + "," + movieID + "," + movieName + "," + bookingCapacity;
        try {
            if (movies.containsKey(movieName)) {
                HashMap<String, Integer> possibleMovie = movies.get(movieName);
                possibleMovie.put(movieID, bookingCapacity);
                movies.put(movieName, possibleMovie);
            } else {
                HashMap<String, Integer> movie = new HashMap<>();
                movie.put(movieID, bookingCapacity);
                movies.put(movieName, movie);
            }


            LoggingHelper.log(this.getClass().getName(), "Add Movie Slots", requestParameters, "Success!", "Success!");
            return "Success";
        } catch (Exception e) {
            System.out.println("Exception is: " + e);
            LoggingHelper.log(this.getClass().getName(), "Add Movie Slots", requestParameters, "Failed!", "Failed!");
            return "Failed";
        }
    }

    @Override
    public void createATWObject() {

    }

    @Override
    public void createOUTObject() {

    }

    @Override
    public void createVERObject() {

    }

    /**
     * Remove Movie slots for ADMIN
     *
     * @param adminID   The admin id who is sending the request.
     * @param movieID   The ID of the movie.
     * @param movieName The name of the movie.
     */

    @Override
    public String removeMovieSlots(String adminID, String movieID, String movieName) {
        String requestParameters = adminID + "," + movieID + "," + movieName;
        try {
            if (movies.get(movieName).containsKey(movieID)) {
                HashMap<String, Integer> possibleMovie = movies.get(movieName);
                movies.get(movieName).remove(movieID);
                LoggingHelper.log(this.getClass().getName(), "Remove Movie Slots", requestParameters, "Success!", "Success!");
                return "Success";
            } else {
                LoggingHelper.log(this.getClass().getName(), "Remove Movie Slots", requestParameters, "Failed!", "Failed!");
                return "Failed";
            }

        } catch (Exception e) {
            System.out.println("Error Occurred while removing movie slot: " + e);
            LoggingHelper.log(this.getClass().getName(), "Remove Movie Slots", requestParameters, "Failed!", "Failed!");
            return "Failed";
        }

    }

    /**
     * The list of movies available for ADMIN
     *
     * @param movieName The name of the movie.
     */
    @Override
    public String listMovieShowsAvailability(String movieName) {

        try {
            if (movies != null) {
                LoggingHelper.log(this.getClass().getName(), "List Movie Shows Availability", movieName, "Success!", "Success!");
                String ans = "";
                HashMap<String, Integer> shows = movies.get(movieName);
                Set<String> keys = shows.keySet();
                System.out.print(movieName + ": ");
                for (String key : keys) {
                    ans += key + ": " + shows.get(key) + "\n";
                }
                return ans;
            } else {
                LoggingHelper.log(this.getClass().getName(), "List Movie Shows Availability", movieName, "Failed!", "Failed!");
                return "";
            }
        } catch (Exception e) {
            System.out.println("Error Occurred! " + e);
            LoggingHelper.log(this.getClass().getName(), "List Movie Shows Availability", movieName, "Failed!", "Failed!");
            return "";
        }
    }

    /**
     * Book movie tickets for customer
     *
     * @param customerID      The customer ID who is sending the request.
     * @param movieID         The ID of the movie.
     * @param movieName       The name of the movie.
     * @param numberOfTickets The number of tickets user wish to book.
     */
    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        String requestParameters = customerID + "," + movieID + "," + movieName + "," + numberOfTickets;
        try {

            if (movies.containsKey(movieName)) {
                HashMap<String, Integer> movie = movies.get(movieName);

                int availableSeats = movie.get(movieID);

                if (availableSeats >= numberOfTickets) {
                    int updatedSeats = availableSeats - numberOfTickets;
                    movie.put(movieID, updatedSeats);

                    movies.put(movieName, movie);


                    String[] details = {movieID, movieName, String.valueOf(numberOfTickets)};

                    if (customerBookings != null) {
                        if (customerBookings.containsKey(customerID)) {
                            List<String[]> currentBookings = customerBookings.get(customerID);

                            for (int i = 0; i < currentBookings.size(); i++) {
                                String[] booking = currentBookings.get(i);
                                if (booking[0].equals(movieID)) {
                                    int total = Integer.parseInt(details[2]) + Integer.parseInt(booking[2]);
                                    details[2] = String.valueOf(total);
                                    currentBookings.remove(booking);
                                }
                            }
                            currentBookings.add(details);


                            customerBookings.put(customerID, currentBookings);
                        } else {
                            List<String[]> newBookings = new ArrayList<>();
                            newBookings.add(details);
                            customerBookings.put(customerID, newBookings);
                        }
                    } else {
                        List<String[]> newBookings = new ArrayList<>();
                        newBookings.add(details);
                        customerBookings.put(customerID, newBookings);
                    }
                    LoggingHelper.log(this.getClass().getName(), "Book Movie Tickets", requestParameters, "Success!", "Success!");
                    return "Success";
                } else {
                    LoggingHelper.log(this.getClass().getName(), "Book Movie Tickets", requestParameters, "Failed!", "Failed!");
                    return "Failed";
                }
            } else {
                LoggingHelper.log(this.getClass().getName(), "Book Movie Tickets", requestParameters, "Failed!", "Failed!");
                return "Failed";
            }
        } catch (Exception e) {
            System.out.println("Error Occurred!" + e);
            LoggingHelper.log(this.getClass().getName(), "Book Movie Tickets", requestParameters, "Failed!", "Failed!");
            return "Failed";
        }
    }

    /**
     * Retrieve the booking schedule for the customer.
     *
     * @param customerID The customer ID who is sending the request.
     */
    @Override
    public String getBookingSchedule(String customerID) {
        String ans = "";
        try {
            if (customerBookings != null) {
                List<String[]> allCustomerBookings = customerBookings.get(customerID);
                LoggingHelper.log(this.getClass().getName(), "Get Booking Schedule", customerID, "Success!", "Success!");

                for (int i = 0; i < allCustomerBookings.size(); i++) {
                    String[] details = allCustomerBookings.get(i);
                    ans += "Movie Name: " + details[1] + " | Show ID: " + details[0] + " | Tickets Booked: " + details[2];
                    ans += "\n";
                }
                return ans;
            }
        } catch (Exception e) {
            System.out.println("Exception in implementer class:" + e);
            LoggingHelper.log(this.getClass().getName(), "Get Booking Schedule", customerID, "Failed!", "Failed!");
        }
        return "";
    }

    /**
     * Cancel the movie tickets for customer
     *
     * @param customerID      The customer ID who is sending the request.
     * @param movieID         The ID of the movie.
     * @param movieName       The name of the movie.
     * @param numberOfTickets The number of tickets user wish to book.
     */
    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        String requestParameters = customerID + "," + movieID + "," + movieName + "," + numberOfTickets;
        try {
            if (movies != null) {
                if (movies.containsKey(movieName)) {
                    HashMap<String, Integer> currentMovie = movies.get(movieName);
                    List<String[]> customerBooking = customerBookings.get(customerID);
                    int bookedSeats = 0, availableSeats, totalSeatsLeft = 0;
                    boolean flag = false;

                    if (customerBooking != null) {
                        for (int i = 0; i < customerBooking.size(); i++) {
                            String[] booking = customerBooking.get(i);


                            if (booking[0].equals(movieID)) {
                                bookedSeats = Integer.parseInt(booking[2]);
                                availableSeats = currentMovie.get(movieID);


                                if (bookedSeats == numberOfTickets) {
                                    customerBooking.remove(booking);
                                    totalSeatsLeft = availableSeats + bookedSeats;
                                } else if (bookedSeats > numberOfTickets) {
                                    bookedSeats = bookedSeats - numberOfTickets;
                                    totalSeatsLeft = availableSeats + numberOfTickets;
                                    String[] updatedRecord = {movieID, movieName, String.valueOf(bookedSeats)};
                                    customerBooking.remove(booking);
                                    customerBooking.add(updatedRecord);
                                } else {
                                    return "Failed";
                                }
                                flag = true;
                                break;
                            }
                        }
                    }

                    if (flag) {

                        customerBookings.put(customerID, customerBooking);

                        currentMovie.put(movieID, totalSeatsLeft);

                        movies.put(movieName, currentMovie);
                        LoggingHelper.log(this.getClass().getName(), "Cancel Movie Tickets", requestParameters, "Success!", "Success!");

                        return "Success";
                    } else {
                        LoggingHelper.log(this.getClass().getName(), "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");
                        return "Failed";
                    }
                }
            } else {
                LoggingHelper.log(this.getClass().getName(), "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");
                return "Failed";
            }
        } catch (Exception e) {
            System.out.println("Error Occurred in implementer class: " + e);
            LoggingHelper.log(this.getClass().getName(), "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");

        }
        LoggingHelper.log(this.getClass().getName(), "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");
        return "Failed";
    }

    @Override
    public String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets) {
        return null;
    }

}


