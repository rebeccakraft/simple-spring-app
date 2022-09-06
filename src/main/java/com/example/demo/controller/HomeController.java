package com.example.demo.controller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RestController
public class HomeController {
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=beckydeniedbrdblob;AccountKey=tDYlBRRfjmv3hEsav138IfJHeOLDbdskdmB6zcvELjaR8kmqFwbQeaFkkiGl/isWo7SxrXmUFYHL+ASty5UCBw==;EndpointSuffix=core.windows.net";
    private static final int DATA_PURGE_DAYS = 3;
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

            readLatestObject(container);

        }
        catch (Exception e)
        {
            results = e.getMessage();
            e.printStackTrace();
        }
        return results;
    }


    private boolean downloadBlobs(CloudBlob blob, String outputDestination)  {
        try { blob.downloadToFile(outputDestination + blob.getName());}

        catch(FileNotFoundException ex){
            System.out.println("File not found: " + outputDestination);
            System.out.println(ex);
            return false;
        } catch(StorageException | IOException ex){
            System.out.println("Could not save file: "+ outputDestination);
            System.out.println(ex);
            return false;
        }
        return true;
    }
    public void readLatestObject(CloudBlobContainer container){
        LocalDate earlier = LocalDate.now().minusDays(DATA_PURGE_DAYS);
        try {
            String earliest = "";
            String newest = "";
            String outputDestination = "/tmp/";
            //this for loop gets name of newest and earliest blob
            for (ListBlobItem blobItem : container.listBlobs()) {
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    //if blob is earlier than all others, it is earliest
                    if(blob.getProperties().getLastModified().before(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                        earliest = blob.getName();
                    }
                    //if blob is newer than other, it is newest
                    if(blob.getProperties().getLastModified().after(Date.from(LocalDate.now().minusYears(2).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                        newest = blob.getName();
                    }
                }
            }
            CloudBlockBlob blobToDelete = container.getBlockBlobReference(earliest);
            CloudBlockBlob blobToDownload = container.getBlockBlobReference(newest);

            if(blobToDelete.getProperties().getLastModified().before(Date.from(earlier.atStartOfDay(ZoneId.systemDefault()).toInstant()))){
                System.out.println("Removing " + earliest + " which is earlier than "+ earlier);
                blobToDelete.deleteIfExists();
            }

            //then check if latest is the same as previous latest
            downloadBlobs(blobToDownload, outputDestination);


        }catch (Exception ex ) {
            System.out.println("Failed to retrieve data file:"+ ex);

        }
    }
}
