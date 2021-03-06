package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class Brewery extends GeneralUser {
    private String types;   //brewery type: bar, pub, etc.
    private List<Beer> beers;

    /* _id is from database, if null is a new brewery */
    public Brewery(@Nullable String _id, String email, String username, String password, String location, String types) {
        super(_id, email, username, password, location, 1);
        this.types = types;
        beers = new ArrayList<>();
    }

    public Brewery(String email, String username, String password, String location, String types) {
        this(null, email, username, password, location, types);
    }

    public Brewery(Document doc) {
        super(doc);
        this.types = doc.get("types") != null ? doc.getString("types") : "--";
        this.beers = new ArrayList<>();
        if (doc.get("beers") != null) {
            for (Document d : doc.getList("beers", Document.class)) {
                beers.add(new Beer(d.getObjectId("beer_id").toString(), d.getString("beer_name")));
            }
        }
    }

    public String getTypes() {
        return types;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setBeers(List<Beer> beers) {
        this.beers = beers;
    }

    public void addBeerToBrewery(Beer beer) {
        beers.add(beer);
    }

    public boolean deleteBeerFromBrewery(Beer beer) {
        for( Beer b : beers){
            if(b.beerID.equals(beer.beerID)){
                return beers.remove(b);
            }
        }
        return false;
    }

    public List<Document> getBeerListDoc() {
        ArrayList<Document> beersList = new ArrayList<>();
        for (Beer b: beers) {
            beersList.add(b.getBeerNameDoc());
        }
        return beersList;
    }

    public List<ObjectId> getBeersID() {
        ArrayList<ObjectId> beerList = new ArrayList<>();
        for (Beer b: beers) {
            beerList.add(new ObjectId(b.getBeerID()));
        }
        return beerList;
    }

    public Document getBreweryDoc(boolean update) {
        Document doc = super.getUserDoc().append("types", types);
        if (update)
            doc.append("beers", getBeerListDoc());
        else    // new Brewery
            doc.append("beers", new ArrayList<>());
        return doc;
    }

}
