package com.example.accesskeybackend.template.controller;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@RestController
@RequestMapping("/api/web/checkIpv6Support")
@AllArgsConstructor
@Tag(name = "IPv6 URI website's handling", description = "Checks the website's answer to IPv6 URI")
public class WebsiteIpv6SupportController {

    @GetMapping("/{siteUrl}")
    @Operation(summary = "Connects to the website using IPv6 URI")
    public boolean isSuccessful(
            @Parameter(description = "The IPv5 URI that is given")
            @PathVariable("siteUrl") String siteUrl) {

        boolean success = false;

        if (parseAndCheckIpv6URI(siteUrl)) {
            int timeout = 10_000_000;
            try {
                URL url = new URL(siteUrl);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(timeout);//set timeout for connection
                connection.connect();

                success = true;
                getOkStatus();

                System.out.println("The connection to website is successful.");
                System.out.println(siteUrl + " :: supports IPv6, content type is: "+ connection.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Operation(summary = "Returns the '200' code status")
    public ResponseEntity<String> getOkStatus() {
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @Operation(summary = "Checks if the provided URI's name corresponds to the IPv6 standards")
    public boolean parseAndCheckIpv6URI(String str) {
        boolean identifier = false;
        IPAddressString addressString = new IPAddressString(str);
        try {
            IPAddress addr = addressString.toAddress();
            IPAddress hostAddr = addressString.toHostAddress();
            Integer prefix = addr.getNetworkPrefixLength();
            if(prefix == null) {
                System.out.println(addr + " has no prefix length");
            } else {
                identifier = true;
                System.out.println(addr + " has host address " + hostAddr + " and prefix length " + prefix);
                System.out.println("Now checking the connection to website.");
            }
        } catch(AddressStringException e) {
            System.out.println(addressString + " is invalid: " + e.getMessage());
        }
        return identifier;
    }
}
