import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Project {


	Map<Integer,Double> spaceMultiplierHM = new HashMap<>();
	Map<Integer,int []> weightHM = new HashMap<>();

	public static void main(String[] args) {
		Project p = new Project();
		File fp = new File("inpFile.txt");
		p.firstPart(fp);

	}

	private void firstPart(File fp) {

		Map<Integer, LinkedHashMap<Integer, Integer>> map = new HashMap<Integer, LinkedHashMap<Integer,Integer>>();
		//Main HashMap with Space as key and a LHM as value
		//LHM has hours of day as key and content iD as value
		
		//Initialise both, HM and LHM with null value
		for(int i=1;i<4;i++){
			map.put(i, null);
			for(int j=0;j<24;j++){
				LinkedHashMap<Integer,Integer> innerMap = map.get(i);
				if(innerMap==null){
					innerMap = new LinkedHashMap<Integer,Integer>();
					map.put(i, innerMap);
				}
				innerMap.put(j, null);
			}
		}
		
		//Space Multiplier HM to store multipliers for each space. Initialise it.
		spaceMultiplierHM.put(1, 2.5);
		spaceMultiplierHM.put(2, 1.5);
		spaceMultiplierHM.put(3, 0.5);

		//Maintaining an array for weights of the contents for each space 
		int [] weights = new int[24];
		Arrays.fill(weights, 0);
		weightHM.put(1, weights);
		weightHM.put(2, weights);
		weightHM.put(3, weights);

		try{

			//Reading each line of I/P 
			BufferedReader br = new BufferedReader(new FileReader(fp));
			String line;
			while ((line = br.readLine()) != null) {
				String [] lineArray = line.split(",");
				//Creating an object of Content with all its properties
				Content contentObj = new Content(Integer.parseInt(lineArray[0]), 
						Integer.parseInt(lineArray[1]), 
						Integer.parseInt(lineArray[2]), 
						Integer.parseInt(lineArray[3]),
						Integer.parseInt(lineArray[4]));

/*	Checking if different contents have been scheduled at the same time at the same space.
	If yes, then depending upon the weights of both contents, the content with higher weight will overwrite the lower one.			
**/
				if(map.get(contentObj.space).get(contentObj.startTime)!=null){
					if(contentObj.weight>= weightHM.get(contentObj.space)[contentObj.startTime])
					{
						//To check when diff Cid is present in same space at same time
						int timeDiff = contentObj.startTime;
						while(timeDiff<contentObj.endTime){
							LinkedHashMap<Integer,Integer> innerMap = map.get(contentObj.space);
							innerMap.put(timeDiff,contentObj.cid);
							map.put(contentObj.space, innerMap);
							int arr[] = weightHM.get(contentObj.space);
							arr[timeDiff] = contentObj.weight;
							weightHM.put(contentObj.space, arr);
							timeDiff++;
						}
					}
				}
/* 
 * 
**/
				else{
					for(int i=1;i<4;i++){
						int timeDiff = contentObj.startTime;
						if(contentObj.space==i ){
							continue;
						}
						else{
							while(timeDiff<contentObj.endTime){
								if(map.get(i).get(timeDiff)!=null){
									if(map.get(i).get(timeDiff)==(contentObj.cid)){
										//Check if same cid is present in any other space at the same time, if not add it
										map = resolveOverLappingProblem(map,contentObj, timeDiff, i);
									}
								}
								else{
									LinkedHashMap<Integer,Integer> innerMap = map.get(contentObj.space);
									innerMap.put(timeDiff,contentObj.cid);
									map.put(contentObj.space, innerMap);
									int arr[] = weightHM.get(contentObj.space);
									arr[timeDiff] = contentObj.weight;
									weightHM.put(contentObj.space, arr);

								}
								timeDiff++;
							}
						}
					}
				}
				/*if(map.get(contentObj.space).get(contentObj.startTime)==null){
					LinkedHashMap<Integer,Integer> innerMap = map.get(contentObj.space);
					int timeDiff=contentObj.startTime;
					while(timeDiff<contentObj.endTime){
						innerMap.put(timeDiff,contentObj.cid);
						map.put(contentObj.space, innerMap);

						timeDiff++;
					}
				}*/
			}
			print(map);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}


	private void print(Map<Integer, LinkedHashMap<Integer, Integer>> map) {
		
	for(int i=1;i<4;i++){
		LinkedHashMap<Integer, Integer> innerMap = map.get(i);
		System.out.println("\nSpace "+i+":-\n");
		for (Map.Entry entry : innerMap.entrySet()) {
		    System.out.println(entry.getKey()+"--->"+entry.getValue()+"");
		}
	}
		
		
		
	}

	private Map<Integer, LinkedHashMap<Integer, Integer>> resolveOverLappingProblem(
			Map<Integer, LinkedHashMap<Integer, Integer>> map, Content contentObj, int timeDiff, int overLappedSpace) 

	{

		// To check when same CID is present in different spaces at same same
		if(spaceMultiplierHM.get(contentObj.space)>spaceMultiplierHM.get(overLappedSpace)){
			LinkedHashMap<Integer,Integer> innerMap = map.get(overLappedSpace);
			innerMap.put(timeDiff, null);

			int weightArr[] = weightHM.get(overLappedSpace);
			weightArr[timeDiff] = 0;
			weightHM.put(overLappedSpace, weightArr);

			map.put(overLappedSpace,innerMap);

			innerMap = map.get(contentObj.space);
			innerMap.put(timeDiff, contentObj.cid);

			weightArr = weightHM.get(contentObj.space);
			weightArr[timeDiff] = contentObj.weight;
			weightHM.put(contentObj.space, weightArr);

			map.put(contentObj.space, innerMap);
		}

		return map;
	}


	class Content{
		int cid;
		int startTime;
		int endTime;
		int weight;
		int space;

		public Content(int cid, int startTime, int endTime, int weight, int space){
			this.cid = cid;
			this.startTime = startTime;
			this.endTime = endTime;
			this.weight = weight;
			this.space = space;
		}
	}

}