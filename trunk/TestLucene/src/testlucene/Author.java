/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlucene;

/**
 *
 * @author tiendv
 */
public class Author {
    
    String name;
    String paperContent;
    public Author(String name, String paperContent) {
        this.name = name;
        this.paperContent = paperContent;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaperContent() {
        return paperContent;
    }

    public void setPaperContent(String paperContent) {
        this.paperContent = paperContent;
    }
    
}
