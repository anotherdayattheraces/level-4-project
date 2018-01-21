package metamap;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;
import entityRetrieval.core.SnomedEntity;
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;



public class MetaMapEntityLinker {
	private List<String> options = new ArrayList<String>();
	private ArrayList<Long> documents;
	private String path;
	
	public MetaMapEntityLinker(ArrayList<Long> documents){
		this.options = setOptions("-A,-K");
		this.documents=documents;
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
	}
	
	public DictionaryHashMap linkArticles(){
		DictionaryHashMap dhm = new DictionaryHashMap();
		Retrieval index=null;
		//Boolean twothree=true;
		try {
			index = RetrievalFactory.instance( path );
		} catch (Exception e1) {
			System.err.println("index not found");
			e1.printStackTrace();
			return null;
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		MetaMapApi api = new MetaMapApiImpl();
		
		api.setOptions(options);
		System.out.println("Found "+documents.size()+" documents relevant to the query");
		int docNo=1;
		for(Long d:documents){
			//if(count++>=50){
		//		break;
		//	}
			Document doc=null;
			try {
				doc = index.getDocument( index.getDocumentName( d ), dc );
			} catch (IOException e) {
				System.err.println("article: "+d+" not contained within index");
				e.printStackTrace();
				return null;
			}
			String documentText = generateString(doc.terms);
			System.out.println(documentText);
			//if(docNo == 11&&twothree){
			//	twothree = false;
			//	continue;
			//}
			System.out.println("Processing document "+docNo++ +" of "+documents.size());
			//if(docNo!=12) continue;
			List<Result> resultList = api.processCitationsFromString(documentText);
			Result result = resultList.get(0);
			try {
				for (Utterance utterance: result.getUtteranceList()) {
					//System.out.println("Utterance:");
					//System.out.println(" Id: " + utterance.getId());
					//System.out.println(" Utterance text: " + utterance.getString());
					//System.out.println(" Position: " + utterance.getPosition());
					//System.out.println("Candidates:");
					for (PCM pcm: utterance.getPCMList()) {
						if(pcm.getMappingList().size()==0) continue;
				          for (Mapping map: pcm.getMappingList()) {
				            for (Ev mapEv: map.getEvList()) {
				              if(mapEv.getPreferredName().length()<3) continue;
				              //System.out.println("   Score: " + mapEv.getScore());
					          //System.out.println("   Preferred Name: " + mapEv.getPreferredName());
					          //System.out.println("   Matched Words: " + mapEv.getMatchedWords());
				              
				              if(!dhm.lookupId(mapEv.getPreferredName(),mapEv.getConceptId())){
				            	  dhm.addEntity(new Entity(mapEv.getPreferredName(),mapEv.getConceptId(),mapEv.getScore(),d));
				              }

				              else{
				            	  dhm.getEntity(mapEv.getPreferredName(), mapEv.getConceptId()).addAppearance(d);;
				              }
}
					}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		System.out.println(dhm.getDictionary().size());
		return dhm;
}
	public static String generateString(List<String> terms){
		StringBuilder sb = new StringBuilder();
		for(String s:terms){
			if(s.contains("%20")){ //links are not to be matched
				continue;
			}
			s = Normalizer.normalize(s, Normalizer.Form.NFD); //normalize all non ascii letters: öäü -> oau
			String resultString = s.replaceAll("[^\\x00-\\x7F]", ""); // replace all non-ascii chars with empty space as metamap doesnt like them
			//s = s.replaceAll("–","-"); //metamap doesnt like first hyphen
			sb.append(resultString+" ");
		}
		return sb.toString();
	}
	
	public List<String> setOptions(String additionalOptions){ //options are in format -a,-b,-c 
		List<String> optionList = new ArrayList<String>();
		optionList.add("-y");
		if(additionalOptions.length()>0){
			String[] options = additionalOptions.split(",");
			for(String s:options) optionList.add(s);
	}
		return optionList;
}
}