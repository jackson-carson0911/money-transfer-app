package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    TransferService transferService = new TransferServiceIMPL();
    AccountService accountService = new AccountServiceIMPL();
    UserService user = new UserServiceIMPL();
    Scanner userInput = new Scanner(System.in);
    //Starts Application
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
    //Prints greeting message
    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    //Prints & Prompts client user to Register or Login
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }
    //Registers New User
    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }
    //Logs-in Existing User
    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        accountService.setToken(currentUser.getToken());
        transferService.setToken(currentUser.getToken());
        user.setToken(currentUser.getToken());
    }
    //Prints MainMenu & Prompts client user to select an action
    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }
    //Prints current balance of user
    private void viewCurrentBalance() {
        System.out.println("Your Current Balance: $" + accountService.findAccountByUserId(currentUser.getUser().getId()).getBalance());
    }

    //Prints transfer history of user.
    private void viewTransferHistory() {
        Map<Integer, String> records = new HashMap<>();
        Transfer[] transfers = transferService.findAll();
        User[] users = user.getUsers();

        //Gathers completed transfers relevant to client user into a list
        int transferCount = 0;
        for (Transfer transfer : transfers) {
            if (transfer.getAccountTo() == accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId() && transfer.getTransferStatusId() == 2) {
                records.put(transfer.getTransferId(), transfer.getTransferId() + "\t" + "To: " + currentUser.getUser().getUsername() + "\t" + transfer.getAmount());
                transferCount++;
            } else if ((transfer.getAccountFrom() == accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId()) && transfer.getTransferStatusId() == 2) {
                records.put(transfer.getTransferId(), transfer.getTransferId() + "\t" + "From: " + currentUser.getUser().getUsername() + "\t" + transfer.getAmount());
                transferCount++;
            }
        }
        if (transferCount != 0) {
            //Prints List header
            System.out.println("-------------------------------------------");
            System.out.println("Transfers");
            System.out.println("ID          From/To                 Amount");
            System.out.println("-------------------------------------------");

            //Prints list of transfers relevant to client user.
            for (Map.Entry<Integer, String> record : records.entrySet()) {
                System.out.println(record.getValue());
            }
            System.out.println("\n");

            if (!records.isEmpty()) {
                //Checks to verify client user provided input exists in the list
                System.out.println("Please enter transfer ID to view details (0 to cancel): ");
                String selectedTransferStr = userInput.next();
                Integer selectedTransfer = Integer.parseInt(selectedTransferStr);
                if (selectedTransfer > 0) {
                    //Validates user input against MapKeys.
                    if (records.containsKey(selectedTransfer)) {
                        Transfer transferView = transferService.findByTransferId(selectedTransfer);
                        System.out.println("-------------------------------------------");
                        System.out.println("Transfer Details");
                        System.out.println("-------------------------------------------");
                        System.out.println("Id: " + transferView.getTransferId());
                        System.out.println("From: " + transferView.getFromUsername());
                        System.out.println("To: " + transferView.getToUsername());
                        System.out.println("Type: " + printType(transferView.getTransferTypeId()));
                        System.out.println("Status: " + printStatus(transferView.getTransferStatusId()));
                        System.out.println("Amount: " + transferView.getAmount());
                    } else {
                        System.out.println("Invalid Selection. Returning to Main Menu.");
                    }
                }
            }
        } else {
            System.out.println("There are no completed transfers for this account.");
        }
    }
    //Prints active pending request for client user.
    private void viewPendingRequests() {

        System.out.println("Would you like to Accept, Reject, or View a transfer (enter 0 to cancel).");
        System.out.println();
        System.out.println("1: Accept");
        System.out.println("2: Reject");
        System.out.println("3: View History");
        System.out.println("0: Cancel");
        System.out.println();

        Transfer[] transfers = transferService.findAll();
        User[] users = user.getUsers();
        Account[] accounts = accountService.findAccounts();

        String userActionStr = userInput.next();

        int userAction = Integer.parseInt((userActionStr));
        //Prompts client user to select a transfer to approve by listed ID
        if (userAction == 1) {
            List<Integer> transferIdList = printHistory(transfers, users);
            System.out.println("Please enter a transfer id from the list above to accept.");
            int transferIdSelected = Integer.parseInt(userInput.next());

            Account withdrawnAcct = new Account();
            Transfer withdrawnTransfer = transferService.findByTransferId(transferIdSelected);
            for(int i = 0; i < accounts.length; i++){
                if (accounts[i].getUserId() == withdrawnTransfer.getFromUserId()) {
                    withdrawnAcct = accounts[i];
                }
            }
            //Confirms client user entered/selected ID exists in list provided
            if (transferIdList.contains(transferIdSelected)) {                
                if (withdrawnAcct.getBalance().compareTo(transferService.findByTransferId(transferIdSelected).getAmount()) > 0) {
                    
                    transferService.approve(transferService.findByTransferId(transferIdSelected));
                    Transfer approvedTransfer = transferService.findByTransferId(transferIdSelected);
                    printUpdateDetails(approvedTransfer);
                    System.out.println("Request Accepted");
                } else {
                    System.out.println("Unable to approve the selected request due to insufficient funds.");
                }
            }else{
                System.out.println("Invalid ID selected. No matching transfers could be found from the list above.");
            }
        //Prompts client user to select a transfer to decline by listed ID
        } else if (userAction == 2) {
            printHistory(transfers, users);
            System.out.println("Please enter a transaction id from the list above to reject.");
            int transferIdSelected = Integer.parseInt(userInput.next());
            transferService.decline(transferService.findByTransferId(transferIdSelected));
            Transfer declinedTransfer = transferService.findByTransferId(transferIdSelected);
            printUpdateDetails(declinedTransfer);
            System.out.println("Request Rejected");
        //Prints all active pending requests relevant to the client user
        } else if (userAction == 3) {
            printHistory(transfers, users);
        //Returns client user to previous menu options
        } else if(userAction == 0) {
            System.out.println("Canceled");
        }else {
            System.out.println("Invalid Input, Returning to the main menu!");
        }
    }

    private void sendBucks() {
        //Prints User list header
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID      Name");
        System.out.println("-------------------------------------------");
        User[] users = user.getUsers();
        Account[] accounts = accountService.findAccounts();

        //Prints list of users to send TEbucks to.
        for (User user : users) {
            System.out.println(user.getId() + "\t" + user.getUsername());
        }
        System.out.println("\n");

        //Prompts user to select the user ID of the recipient.
        System.out.println("Enter ID of user you are sending to (0 to cancel):");
        String receiverIdStr = userInput.next();
        int receiverId = Integer.parseInt(receiverIdStr);
        Account receiverAcct = new Account();
        for(int i = 0; i < accounts.length; i++){
            if (accounts[i].getUserId() == receiverId) {
                receiverAcct = accounts[i];
            }
        }

          //Validate client user is not sending TEbucks to themself
          if (currentUser.getUser().getId() != receiverId) {
            //Prompts user for the amount to send
            System.out.println("Enter amount:");
            userInput.nextLine();

            BigDecimal amountSent = BigDecimal.valueOf(Long.parseLong(userInput.next()));
            //Prevents client user from sending TEbucks with insufficient funds.
            if (amountSent.compareTo(new BigDecimal(0)) > 0) {
                transferService.setToken(currentUser.getToken());

                Transfer send = transferService.createRequest(new Transfer(2, 2, receiverAcct.getAccountId(),
                        accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId(), (amountSent)));

                //Prints created transfer request fulfillment details for client user review
                System.out.println("-------------------------------------------");
                System.out.println("Updated Transfer Details");
                System.out.println("-------------------------------------------");
                System.out.println("Id: " + send.getTransferId());
                System.out.println("From: " + send.getFromUsername());
                System.out.println("To: " + send.getToUsername());
                System.out.println("Type: " + printType(send.getTransferTypeId()));
                System.out.println("Status: " + printStatus(send.getTransferStatusId()));
                System.out.println("Amount: " + send.getAmount());
                System.out.println("Bucks successfully sent to: " + send.getToUsername());
            } else {
                System.out.println("You must enter an amount greater than 0!");
            }
        } else {
            System.out.println("Invalid Id selected, cannot send bucks to yourself!");
        }
    }

    private void requestBucks() {
        //Prints User list header
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID      Name");
        System.out.println("-------------------------------------------");
        User[] users = user.getUsers();
        Account[] accounts = accountService.findAccounts();
        Account recipientAcct = new Account();

        //Prints list of users to request TEbucks from.
        for (User u : users) {
            System.out.println(u.getId() + "\t" + u.getUsername());
        }
        System.out.println("\n");


        //Prompts user to select the user ID of the request recipient.
        System.out.println("Enter ID of user you are requesting from (0 to cancel):");
        String recipientIdString = userInput.next();
        int recipientId = Integer.parseInt(recipientIdString);

        if (currentUser.getUser().getId() != recipientId) {
            //Prompts user for the amount to request.
            System.out.println("Enter amount:");
            userInput.nextLine();

            BigDecimal amountRequested = BigDecimal.valueOf(Long.parseLong(userInput.next()));

                for(int i = 0; i < accounts.length; i++){
                if (accounts[i].getUserId() == recipientId) {
                    recipientAcct = accounts[i];
                    break;
                }
            }
            //Validates amount requested is greater than 0.
            if (amountRequested.compareTo(new BigDecimal(0)) > 0) {
                transferService.setToken(currentUser.getToken());

                Transfer request = transferService.createRequest(new Transfer(1, 1, 
                                    accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId(),
                                    recipientAcct.getAccountId(), (amountRequested)));

                //Prints transfer request details for client user review
                System.out.println("-------------------------------------------");
                System.out.println("Updated Transfer Details");
                System.out.println("-------------------------------------------");
                System.out.println("Id: " + request.getTransferId());
                System.out.println("From: " + request.getFromUsername());
                System.out.println("To: " + request.getToUsername());
                System.out.println("Type: " + printType(request.getTransferTypeId()));
                System.out.println("Status: " + printStatus(request.getTransferStatusId()));
                System.out.println("Amount: " + request.getAmount());
                System.out.println("Request successfully created: " + request.getFromUsername());
            } else {
                System.out.println("You must enter an amount greater than 0!");
            }
        } else {
            System.out.println("Invalid Id selected, cannot request bucks to yourself!");
        }

    }


    // method to convert Transfer Type IDs to string format
    private String printType(int transferTypeId) {
        String stringType;
        if (transferTypeId == 1) {
            stringType = "Request";
        } else {
            stringType = "Send";
        }
        return stringType;
    }

    // method to convert Transfer Status IDs to string format
    private String printStatus(int transferStatusId) {
        String stringStatus;
        if (transferStatusId == 1) {
            stringStatus = "Pending";
        } else if (transferStatusId == 2) {
            stringStatus = "Approved";
        } else {
            stringStatus = "Rejected";
        }
        return stringStatus;
    }

    private List<Integer> printHistory(Transfer[] transfers, User[] users) {
        List<Integer> transferIds = new ArrayList<>();
        //Prints pending transfers header
        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID          To                 Amount");
        System.out.println("-------------------------------------------");

        //Prints each transfer relevant to the client user (user's Account ID appears in either TO or FROM fields of transaction)
        for (Transfer transfer : transfers) {
            if (transfer.getTransferStatusId() == 1 && transfer.getTransferTypeId() == 1) {
                if (transfer.getAccountFrom() == accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId()) {
                    System.out.println(transfer.getTransferId() + "\t" + "To: " + transfer.getToUsername() + "\t" + transfer.getAmount());
                    transferIds.add(transfer.getTransferId());
                } else if (transfer.getAccountTo() == accountService.findAccountByUserId(currentUser.getUser().getId()).getAccountId()) {
                    System.out.println(transfer.getTransferId() + "\t" + "From: " + transfer.getFromUsername() + "\t" + transfer.getAmount());
                    transferIds.add(transfer.getTransferId());
                }
            }
        }
        return transferIds;
    }

    //method to print updated details for client user review
    private void printUpdateDetails(Transfer transfers){
        System.out.println("-------------------------------------------");
        System.out.println("Updated Transfer Details");
        System.out.println("-------------------------------------------");
        System.out.println("Id: " + transfers.getTransferId());
        System.out.println("From: " + transfers.getFromUsername());
        System.out.println("To: " + transfers.getToUsername());
        System.out.println("Type: " + printType(transfers.getTransferTypeId()));
        System.out.println("Status: " + printStatus(transfers.getTransferStatusId()));
        System.out.println("Amount: " + transfers.getAmount());
    }
}



