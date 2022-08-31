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
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

            // Create the container if it does not exist.
            final String filePath = "C:\Users\228161\OneDrive - American Airlines, Inc\Desktop\test1.txt"
            final String filePath1 = "C:\Users\228161\OneDrive - American Airlines, Inc\Desktop\test2.txt"

            // Create or overwrite the "myimage.jpg" blob with contents from a local file.
            CloudBlockBlob blob = container.getBlockBlobReference("test1.txt");
            File source = new File(filePath);
            blob.upload(new FileInputStream(source), source.length());

            CloudBlockBlob blob1 = container.getBlockBlobReference("test2.txt");
            File source1 = new File(filePath1);
            blob1.upload(new FileInputStream(source1), source1.length());
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }

        return "files were added as blobs";
    }
}
