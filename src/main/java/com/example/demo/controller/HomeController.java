package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

@RestController
public class HomeController {
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=beckydeniedbrdblob;AccountKey=tDYlBRRfjmv3hEsav138IfJHeOLDbdskdmB6zcvELjaR8kmqFwbQeaFkkiGl/isWo7SxrXmUFYHL+ASty5UCBw==;EndpointSuffix=core.windows.net";
    @GetMapping("/")
    public String home() {
        String results = "blobs have been saved to C://mydownloads test";
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
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    blob.downloadToFile("C:\\mydownloads\\" + blob.getName());
                }
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
