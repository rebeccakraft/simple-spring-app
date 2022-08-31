package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.FileInputStream;

@RestController
public class HomeController {
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=beckydeniedbrdblob;AccountKey=tDYlBRRfjmv3hEsav138IfJHeOLDbdskdmB6zcvELjaR8kmqFwbQeaFkkiGl/isWo7SxrXmUFYHL+ASty5UCBw==;EndpointSuffix=core.windows.net";
    @GetMapping("/")
    public String home() {
        String results = "";
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

            // Loop over blobs within the container and output the URI to each of them.
            for (ListBlobItem blobItem : container.listBlobs()) {
                System.out.println(blobItem.getUri());
                results += blobItem.getUri();
   }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }

        return results;
    }
}
