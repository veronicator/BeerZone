package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.GeneralUser;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class UserManager {
    private final MongoManager mongoManager;
    private MongoCollection<Document> users;
        // users collection include both standard users (type 0) and breweries (type 1)

    public UserManager() {
        mongoManager = MongoManager.getInstance();
        users = mongoManager.getCollection("users");
    }

    public void addBrewery(String username, String password, String email, String location, String types) {
        Document doc = new Brewery(email, username, password, location, types).getBreweryDoc(false);
        registerUser(doc);
    }

    public void addUser(String username, String password, String email, String location, int age) {
        Document doc = new StandardUser(email, username, password, age, location).getUserDoc(false);
        registerUser(doc);
    }

    public void registerUser(Document userDoc) {
        try {
            users.insertOne(userDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean login(String email, String password) {
        boolean ok = false;
        try {
            Document userDoc = users.find(eq("email", email)).first();
            if (userDoc == null || userDoc.isEmpty())
                return false;

            ok = new GeneralUser(userDoc).checkPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public void deleteUser(String email) {
        try {
            users.deleteOne(eq("email", email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUsername(String email, String newUsername) {
        UpdateResult result = users.updateOne(eq("email", email), set("username", newUsername));
    }

}
