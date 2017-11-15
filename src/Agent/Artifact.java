/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

/**
 *
 * @author srinivaik
 */
public class Artifact implements java.io.Serializable{
    private final int id;
    private String name;
    private String creator;
    private String genre;

    public Artifact(int id, String name, String creator, String genre) {
        this.id = id;
        this.name = name; 
        this.creator = creator;
        this.genre = genre; 
    }

    public int getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }
}