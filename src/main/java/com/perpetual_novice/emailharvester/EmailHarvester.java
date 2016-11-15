package com.perpetual_novice.emailharvester;

import com.perpetual_novice.emailharvester.managers.AssetManager;
import com.perpetual_novice.emailharvester.managers.CrawlingManager;
import com.perpetual_novice.emailharvester.managers.WebSearchManager;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import com.perpetual_novice.emailharvester.util.ZipBuilder;
import com.perpetual_novice.emailharvester.sql.SQLConnectionManager;

public class EmailHarvester {

    public static void main(String[] args) {
        HashMap<String, Vector<String>> arguments = new HashMap<String, Vector<String>>();

        arguments.put("-maxThreads", new Vector<String>());
        arguments.get("-maxThreads").add("20");
        arguments.put("-host", new Vector<String>());
        arguments.get("-host").add("localhost");
        arguments.put("-port", new Vector<String>());
        arguments.get("-port").add("5432");
        arguments.put("-desc", new Vector<String>());
        arguments.get("-desc").add("default");
        arguments.put("-cities", new Vector<String>());
        arguments.get("-cities").add("C:\\Users\\ialsmadi\\Downloads\\zip_codes_states11.csv");
        arguments.put("-d", new Vector<String>());
        arguments.get("-d").add("postgres");
         arguments.put("-u", new Vector<String>());
        arguments.get("-u").add("postgres");
         arguments.put("-p", new Vector<String>());
        arguments.get("-p").add("7771");
        arguments.put("-k", new Vector<String>());
        arguments.get("-k").add("San Antonio");

        /*
         *  collect args from the command line and set in associative array
         *  command line arguments should be in the form:
         *  
         *  java eastertheharvester -maxThreads 20 -d databasename -host databasehost -port databaseport -u databaseuser -p databasepassword -k keywords with space between -schema -cities fromfile
         *  
         */
        for (int x = 0; x < args.length; x++) {

            // look for flags
            if (args[x].startsWith("-")) {
                String flagString = args[x];

                // look ahead for non flag arguments
                Vector<String> found = new Vector<String>();
                boolean flag = false;
                int y = x + 1;
                while (flag == false && y < args.length) {
                    if (!args[y].startsWith("-")) {
                        found.add(args[y]);
                        x = y;
                    } else {
                        break;
                    }
                    y++;
                }

                arguments.put(flagString, found);
            }
        }

        // create sql connection manager
        boolean create = arguments.containsKey("-schema");
        SQLConnectionManager connectionManager = new SQLConnectionManager(arguments.get("-host").firstElement(),
                arguments.get("-port").firstElement(),
                arguments.get("-d").firstElement(),
                arguments.get("-u").firstElement(),
                arguments.get("-p").firstElement(),
                arguments.get("-desc").firstElement(),
                arguments.get("-k"),
                create);
        Stack<Thread> threadStack = new Stack<Thread>();

        AssetManager assets = new AssetManager(connectionManager);

        /*
		 * collect all cities and store in asset manager
		 * 
         */
       if (arguments.containsKey("-cities")) {
            System.out.println("San Antonio");
            threadStack.push(new Thread(new ZipBuilder(connectionManager, arguments.get("-cities").firstElement(), assets)));
            threadStack.peek().start();
        } else {
            assets.innitFromDB();

            System.out.println("Locations remaining: " + assets.countLocations());
          //  assets.collectAllLocations();
            assets.finalizeLocations();
        }

        /*
		 * wait until there are locations to be handled
		 * if 10 seconds elapse, continue with nothing
         */
        System.out.println("Proceeding with search");
        int count = 0;
        while (count < 100 && assets.countLocations() == 0) {
            try {
                count++;
                Thread.sleep(100);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (assets.countLocations() > 0) {
            /*
			 * start web search thread
			 * it runs in its own thread so that it can auto throttle web searches via search engines
             */
            threadStack.push(new Thread(new WebSearchManager(assets, arguments.get("-k"))));
            threadStack.peek().start();

            /*
			 * start a thread that adds and removes crawlers as websites become available
             */
            threadStack.push(new Thread(new CrawlingManager(assets, Integer.parseInt(arguments.get("-maxThreads").firstElement()), connectionManager)));
            threadStack.peek().start();
        }
    }
}
