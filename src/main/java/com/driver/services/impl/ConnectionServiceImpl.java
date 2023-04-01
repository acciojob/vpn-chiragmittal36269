package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        //Connect the user to a vpn by considering the following priority order.
        //1. If the user is already connected to any service provider, throw "Already connected" exception.
        //2. Else if the countryName corresponds to the original country of the user, do nothing.
        // This means that the user wants to connect to its original country, for which we do not require a connection. Thus, return the user as it is.
        //3. Else, the user should be subscribed under a serviceProvider having option to connect to the given country.
        //If the connection can not be made (As user does not have a serviceProvider or serviceProvider does not have given country, throw "Unable to connect" exception.
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId" and return the updated user.
        // If multiple service providers allow you to connect to the country, use the service provider having smallest id.

        countryName = countryName.toUpperCase();
        User user = userRepository2.findById(userId).get();
        if (user.getConnected()) {
            throw new Exception("Already connected");
        }
        String currentCountry = String.valueOf(user.getOriginalCountry().getCountryName());
        if (currentCountry.equals(countryName)) {
            return user;
        }
//        if(String.valueOf(user.getCountry().getCountryName()).equals(countryName))
//        {
//            return user;
//        }
        if(user.getServiceProviderList() == null) {
            throw new Exception("Unable to connect");
        }

        List<ServiceProvider> updatedServiceProvider = new ArrayList<>();
        boolean marker = true;
        ServiceProvider realProvider = null;
        int max = Integer.MAX_VALUE;
        Country country = null;
        List<ServiceProvider> userServiceProviderList = user.getServiceProviderList();

        for (ServiceProvider s : userServiceProviderList) {
            List<Country> countryList = s.getCountryList();
            for (Country c : countryList) {
                if (c.getCountryName().toString().equals(countryName) && max > s.getId()) {
                    max = s.getId();
                    realProvider = s;
                    country = c;
                    marker = false;
                }
            }
        }
        if (marker) {
            throw new Exception("Unable to connect");
        }

//        String s = countryName.toUpperCase();
//        switch (s) {
//            case "IND":
//                user.getCountry().setCountryName(CountryName.IND);
//                user.setMaskedIp("001");
//                break;
//            case "USA":
//                user.getCountry().setCountryName(CountryName.USA);
//                user.setMaskedIp("002");
//                break;
//            case "AUS":
//                user.getCountry().setCountryName(CountryName.AUS);
//                user.setMaskedIp("003");
//                break;
//            case "CHI":
//                user.getCountry().setCountryName(CountryName.CHI);
//                user.setMaskedIp("004");
//                break;
//            default:
//                user.getCountry().setCountryName(CountryName.JPN);
//                user.setMaskedIp("005");
//                break;
//        }
        Connection connection = new Connection();
        connection.setServiceProvider(realProvider);
        connection.setUser(user);

        user.setMaskedIp(country.getCode() + "." + realProvider.getId() + "." + user.getId());
        user.setConnected(true);
        user.getConnectionList().add(connection);

        realProvider.getConnectionList().add(connection);

        serviceProviderRepository2.save(realProvider);
        return userRepository2.save(user);
    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();
        if(!user.getConnected())
        {
            throw new Exception("Already disconnected");
        }

        user.setMaskedIp(null);
        user.setConnected(false);
//        String s = user.getOriginalIp();
//        switch (s) {
//            case "001":
//                user.getCountry().setCountryName(CountryName.IND);
//                break;
//            case "002":
//                user.getCountry().setCountryName(CountryName.USA);
//                break;
//            case "003":
//                user.getCountry().setCountryName(CountryName.AUS);
//                break;
//            case "004":
//                user.getCountry().setCountryName(CountryName.CHI);
//                break;
//            default:
//                user.getCountry().setCountryName(CountryName.JPN);
//                break;
//        }

        return userRepository2.save(user);
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender;
        User receiver;
        try {
            sender = userRepository2.findById(senderId).get();
            receiver = userRepository2.findById(receiverId).get();
        } catch (Exception e) {
            throw new Exception("Cannot establish communication");
        }
        return sender;
    }
}
