package com.github.Emcc13.MendingTools.Config;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BlueprintConfig {
    private Map<Integer, MendingBlueprint> blueprints;
    public BlueprintConfig(String conf_file_name) {
        this.blueprints = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        NodeList XMLblueprints;
        try {
            db = dbf.newDocumentBuilder();
            Document customXML = db.parse(new File(
                    MendingToolsMain.getInstance().getDataFolder().getAbsolutePath(),conf_file_name));
            XMLblueprints = customXML.getDocumentElement().getElementsByTagName("blueprint");
            for (int idx=0; idx<XMLblueprints.getLength(); idx++) {
                MendingBlueprint blueprint = new MendingBlueprint((Element) XMLblueprints.item(idx));
                this.blueprints.put(blueprint.getID(), blueprint);
            }
        } catch (ParserConfigurationException e){
            Bukkit.getLogger().log(Level.SEVERE,"[ERROR MT] Failed to load any blueprints!");
        } catch (IOException | SAXException e){
            Bukkit.getLogger().log(Level.SEVERE,"[MT] Failed to load custom blueprints.");
            Bukkit.getLogger().log(Level.WARNING,"[MT] Loading default blueprints ...");
            String default_file_name = "/blueprints.xml";
            InputStream is = getClass().getResourceAsStream(default_file_name);
            try{
                db = dbf.newDocumentBuilder();
                Document defaultXML = db.parse(is);
                XMLblueprints = defaultXML.getDocumentElement().getElementsByTagName("blueprint");
                for (int idx=0; idx<XMLblueprints.getLength(); idx++) {
                    MendingBlueprint blueprint = new MendingBlueprint((Element) XMLblueprints.item(idx));
                    this.blueprints.put(blueprint.getID(), blueprint);
                }
                Document saveDoc = db.newDocument();
                Element root = saveDoc.createElement("blueprints");
                saveDoc.appendChild(root);
                for (MendingBlueprint blueprint : this.blueprints.values()){
                    blueprint.addToConf(root, saveDoc);
                }
                TransformerFactory transformerF = TransformerFactory.newInstance();
                Transformer transformer = transformerF.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(saveDoc);
                StreamResult result = new StreamResult(new File(MendingToolsMain.getInstance().getDataFolder().getAbsolutePath(),"blueprints.xml"));
                transformer.transform(source, result);
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException | IllegalArgumentException e2){
                Bukkit.getLogger().log(Level.SEVERE,"[ERROR MT] Failed to load any blueprints!");
            }
        }




//        File customXML = new File(MendingToolsMain.getInstance().getDataFolder().getAbsolutePath(),"blueprints.xml");

//        InputStream is = getClass().getResourceAsStream(default_file_name);
//        try{
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            Document doc = db.parse(is);
//            NodeList XMLblueprints = doc.getDocumentElement().getElementsByTagName("blueprint");
//            for (int idx=0; idx<XMLblueprints.getLength(); idx++){
//                MendingBlueprint blueprint = new MendingBlueprint((Element) XMLblueprints.item(idx));
//                this.blueprints.put(blueprint.getID(), blueprint);
//            }
//
//            DocumentBuilder db2 = dbf.newDocumentBuilder();
//            Document doc2 = db2.newDocument();
//            Element root = doc2.createElement("blueprints");
//            doc2.appendChild(root);
//            for (Map.Entry<Integer, MendingBlueprint> entry : this.blueprints.entrySet()){
//                entry.getValue().addToConf(root, doc2);
//            }
//
//            TransformerFactory transformerF = TransformerFactory.newInstance();
//            Transformer transformer = transformerF.newTransformer();
//            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc2);
//            StreamResult result = new StreamResult(new File(MendingToolsMain.getInstance().getDataFolder().getAbsolutePath(),"blueprints.xml"));
//            transformer.transform(source, result);
//        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e){
//            e.printStackTrace();
//        }
    }

    public Map<Integer, MendingBlueprint> getBlueprints(){
        return this.blueprints;
    }
}
