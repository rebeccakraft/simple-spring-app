package com.example.demo.controller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RestController
public class HomeController {
    String storageConnectionString = System.getenv("connectionString");
    String containerName = System.getenv("containerName");
    private static final int DATA_PURGE_DAYS = 3;
    String results = " ";
    private String filenamePrefix = "test";

    @GetMapping("/")
    public String home() {
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            // Loop over blobs within the container and output the URI to each of them.

            readLatestObject(container);

        }
        catch (Exception e)
        {
            results += e.getMessage();
            e.printStackTrace();
        }
        return results;
    }


    private boolean downloadBlobs(CloudBlob blob, String outputDestination)  {
        try { blob.downloadToFile(outputDestination + blob.getName());}

        catch(FileNotFoundException ex){
            results+= "File not found exception";
            System.out.println("File not found: " + outputDestination);
            System.out.println(ex);
            return false;
        } catch(StorageException | IOException ex){
            results+= "Storage IO found exception";
            System.out.println("Could not save file: "+ outputDestination);
            System.out.println(ex);
            return false;
        }
        return true;
    }
    public void readLatestObject(CloudBlobContainer container){
        try {
            String newest = "";
            String outputDestination = "/tmp/";
            Date newer= Date.from(LocalDate.now().minusYears(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
            //this for loop gets name of newest blob
            for (ListBlobItem blobItem : container.listBlobs(filenamePrefix, true)) {
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    System.out.println(((CloudBlob) blobItem).getName());
                    CloudBlob blob = (CloudBlob) blobItem;
                    if(blob.getProperties().getLastModified().after(newer)) {
                        newest = blob.getName();
                        newer = blob.getProperties().getLastModified();
                    }
                }
            }

            CloudBlockBlob blobToDownload = container.getBlockBlobReference(newest);
            System.out.println("got newest");

            if(blobToDownload.exists()){
                System.out.println("Blob exists");
            }
            else{
                System.out.println("blob doesnt exist");
            }

            //then check if latest is the same as previous latest
            boolean rc = downloadBlobs(blobToDownload, outputDestination);
            if (rc) results += " downloaded the blob to "+ outputDestination;

        }catch (Exception ex ) {
            System.out.println("Failed to retrieve data file:"+ ex);

        }
    }
}
