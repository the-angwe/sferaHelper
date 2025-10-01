package com.botov.sferaHelper;

import com.google.gson.Gson;
import lombok.Data;

public class Test {

    @Data
    public static class UserDto {
        private String name;
        private User2Dto user2Dto;
    }

    @Data
    public static class User2Dto {
        private String name;
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        UserDto user = new UserDto();
        user.setName("a jnf vjakfnd lajkdfn ndalfk bjna ,cmafn ;d kn;sd");
        user.setUser2Dto(new User2Dto());
        user.getUser2Dto().setName("asdga fdk mnfd jnkfdsa jnlasjkg");
        String jsonString = gson.toJson(user);
        System.out.println(jsonString);
    }

}
