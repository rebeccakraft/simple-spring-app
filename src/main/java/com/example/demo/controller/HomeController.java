package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Date;

@RestController
public class HomeController {
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=beckydeniedbrdblob;AccountKey=tDYlBRRfjmv3hEsav138IfJHeOLDbdskdmB6zcvELjaR8kmqFwbQeaFkkiGl/isWo7SxrXmUFYHL+ASty5UCBw==;EndpointSuffix=core.windows.net";
    @GetMapping("/")
    public String home() {
        String results = "blobs have been saved to C://mydownloads test";
        Date lastModified = new Date();
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

           // BlobContainerProperties proper = container.getProperties();
          //  lastModified = proper.getLastModified();
            // Loop over blobs within the container and output the URI to each of them.
            for (ListBlobItem blobItem : container.listBlobs()) {
                // If the item is a blob, not a virtual directory.
                results += blobItem.getUri() + " ";
            }
        }
        catch (Exception e)
        {
            results = e.getMessage();
            e.printStackTrace();
        }

        return results;
    }
}
