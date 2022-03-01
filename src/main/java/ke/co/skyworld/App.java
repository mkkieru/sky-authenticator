/*
package ke.co.skyworld;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ke.co.skyworld.dao.Sql2oUsersDao;
import ke.co.skyworld.exception.ApiException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.Spark.after;

public class App {
    public static void main(String[] args) {

        Sql2oUsersDao usersDao = new Sql2oUsersDao();
        Gson gson = new Gson();

        //CREATE NEW USER
        post("/users/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.user (first_name,last_name,national_id,password) VALUES (:first_name,:last_name,:national_id,:password)";
            System.out.println("req.response"+ req.body());

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });


        //CREATE NEW AUTHENTICATION DETAILS
        post("/auth_details/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.auth_details (user_id,program_id,auth_code,time_to_live,time_to_live_units) VALUES (:user_id,:program_id,:auth_code,:time_to_live,:time_to_live_units)";
            System.out.println("req.response"+ req.body());

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });

        //CREATE NEW BUFFER
        post("/buffer/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.buffer (program_id,token,identifier_type,identifier) VALUES (:program_id,:token,:identifier_type,:identifier)";
            System.out.println("req.response"+ req.body());

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });

        //CREATE NEW IDENTIFIER
        post("/identifier/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.identifier (identifier_type,identifier) VALUES (:identifier_type,:identifier)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });

        //CREATE NEW IDENTIFIER TYPE
        post("/identifier_type/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.identifier_type (identifier_type) VALUES (:identifier_type)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });

        //INSERT A NEW PROGRAM
        post("/programs/new", "application/json", (req, res) -> {
            String sqlQuery = "INSERT INTO public.programs (program_id,program_name) VALUES (:program_id,:program_name)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            usersDao.add(sqlQuery,valuesMap);

            res.status(201);
            return gson.toJson(req.body());
        });

        //UPDATE PROGRAMS
        post("/programs/update", "application/json", (req, res) -> {

            String sqlQuery = "UPDATE public.ke.co.skyworld.programs SET program_name = (:program_name) WHERE program_id = (:program_id)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            //usersDao.update(sqlQuery,valuesMap);

            return gson.toJson(req.body());
        });

        //UPDATE IDENTIFIER
        post("/identifier/update", "application/json", (req, res) -> {

            String sqlQuery = "UPDATE public.identifier SET identifier_type = (:identifier_type), identifier = (:identifier)  WHERE identifier_id = (:identifier_id)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            //usersDao.update(sqlQuery,valuesMap);

            return gson.toJson(req.body());
        });

        //UPDATE USER DETAILS
        post("/user/update", "application/json", (req, res) -> {

            String sqlQuery = "UPDATE public.user SET first_name = (:first_name), last_name =(:last_name), national_id = (:national_id) WHERE user_id = (:user_id)";

            Type type = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            LinkedHashMap<String, Object> valuesMap = new Gson().fromJson(req.body(), type);
            //usersDao.update(sqlQuery,valuesMap);

            return gson.toJson(req.body());
        });

        //GET ALL REGISTERED USERS
        get("/users", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.user";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"I'm sorry, but no users are currently listed in the database.\"}";
            }
        });

        get("/identifier_type", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.identifier_type";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"I'm sorry, but no identifier_types are currently listed in the database.\"}";
            }
        });

        get("/auth_details", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.auth_details";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"No auth_details have been sent yet.\"}";
            }
        });

        get("/buffer", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.buffer";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"Buffer is currently empty.\"}";
            }
        });

        get("/identifier", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.identifier";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"I'm sorry, but no identifiers are currently listed in the database.\"}";
            }
        });


        get("/programs", "application/json", (req, res) -> {
            String sqlQuery = "SELECT * FROM public.ke.co.skyworld.programs";
            if(usersDao.getAll(sqlQuery).size() > 0){
                return gson.toJson(usersDao.getAll(sqlQuery));
            }
            else {
                return "{\"message\":\"I'm sorry, but no ke.co.skyworld.programs are registered in the database.\"}";
            }
        });



        exception(ApiException.class, (exc, req, res) -> {
            ApiException err = (ApiException) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatusCode());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json"); //after does not run in case of an exception.
            res.status(err.getStatusCode()); //set the status
            res.body(gson.toJson(jsonMap));  //set the output.
        });


        after((req, res) -> res.type("application/json"));
    }
}

*/
