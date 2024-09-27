package com.techelevator.tenmo.model;
//create a constructor, getters, setters and a no args constructor for the userDTO to be called on.
public class UserDto {

   private int Id;
   private String username;


   public UserDto() { }

   
   public UserDto(int Id, String username) {
      this.Id = Id;
      this.username = username;
   }

   public int getId() {
      return Id;
   }

   public void setId(int Id) {
      this.Id = Id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }



}
