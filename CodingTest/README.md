**Integration API :**

1. You'll need to fetch all the pages in the Confluence space.


2. API details can be found here: https://developer.atlassian.com/cloud/confluence/rest/v2/intro/#about


3. Expose a GET API **"/fetchPages"** that will fetch all the pages and it's contents in JSON format.


4. Once the above API is complete, expose an API **"/search"** that will return all pages that contains the searched string
   

4. A high level structure is provided in this project, you are free to make changes as per your judgement.


5. This application runs on port 8080. 
   1. You can check if the application is running using the url: http://localhost:8080/heartBeat
   2. Application is running if it returns "Integration App is running smoothly ...!!!"
   3. You can now proceed with your coding.


6. Please ensure that the following points are taken care of: 
   1. Follow Java Coding & Naming standards
   2. Do not hardcode any values, unless required
   3. Add comments to your code
   4. Do proper Exception handling


7. Your code would be evaluated on the following parameters
   1. Is the code working, this is the primary criteria
   2. How efficient is the code i.e. how quick are the results returned by the API
   3. Proper coding standards, logging, comments, etc.

