//William Dahl
//ICSI 401- Algorithms
// March 1st 2018

package csi403;


// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;


// Extend HttpServlet class
public class Project2 extends HttpServlet {

  // Standard servlet method 
  public void init() throws ServletException
  {
      // Do any required initialization here - likely none
  }

  // Standard servlet method - we will handle a POST operation
  public void doPost(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      doService(request, response); 
  }

  // Standard servlet method - we will not respond to GET
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type and return an error message
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println("{ 'message' : 'Use POST!'}");
  }


  // Our main worker method
  // Parses messages e.g. {"inList" : [5, 32, 3, 12]}
  // Returns the list reversed.   
  private void doService(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type to be JSON
      response.setContentType("application/json");
      // Send back the response JSON message
      PrintWriter out = response.getWriter();
      try{
        // Get received JSON data from HTTP request
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonStr = "";
        if(br != null){
          jsonStr = br.readLine();
        }
      
        // Create JsonReader object
        StringReader strReader = new StringReader(jsonStr);
        JsonReader reader = Json.createReader(strReader);

        // Get the singular JSON object (name:value pair) in this message.    
        JsonObject obj = reader.readObject();
        // From the object get the array named "inList"
        JsonArray inArray = obj.getJsonArray("inList");
        //Creates a Priority queue object
        PriorityQueue queue = new PriorityQueue();

        //loops through json array
        for(int i = 0;i < inArray.size(); i++){
          //Sets the Json object equal to the object in the Json array at i
          obj = inArray.getJsonObject(i);
          //checks if the command is enqueue
          if(obj.getString("cmd").equals("enqueue")){
            //adds the priorty node to the priority queue
            queue.enqueue(obj.getString("name"), obj.getInt("pri"));
          } 

          //checks if the command is dequeue
          else if(obj.getString("cmd").equals("dequeue")){
            // checks if the priority queue is empty
            if(queue.isEmpty()){
              out.println("list is empty, cannot dequeue");
            }

            //dequeue the head
            else{
              queue.dequeue();
            }
          }

          //invlaid command
          else{
            out.println("invalid command given");
          }
        }
 
        //builds the json array builder for output
        JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();
        //loops through the priority queue
        for(int i = 0; i < queue.getSize(); i++){
          outArrayBuilder.add(queue.print());
        }

        //prints the priority queue
        out.println("{ \"outList\" : " + outArrayBuilder.build().toString() + "}"); 
      }
      //catch for json exceptiom
      catch(JsonException e){
        out.println(e.getMessage());
      }
  }

    
  // Standard Servlet method
  public void destroy()
  {
      // Do any required tear-down here, likely nothing.
  }
}

//Node in the priorty queue
class PriorityNode{
  //fileds of the node
  private String name; // holds the name of the node
  private int priority; // holds the prioity of the node
  private PriorityNode next; // references the next node being pointed to

  //itializes the node fileds
  public PriorityNode(String name, int priority){
    super();// calls default method
    this.name = name;
    this.priority = priority;
  }

  //sets name
  public void setName(String name){
    this.name = name;
  }

  //sets prioroity
  public void setPriority(int priority){
    this.priority = priority;
  }

  //sets next reference
  public void setNext(PriorityNode next){
    this.next = next;
  }

  //sets name
  public String getName(){
    return name;
  }

  //gets prioity
  public int getPriority(){
    return priority;
  }

  //gets next refernce
  public PriorityNode getNext(){
    return next;
  }
}

//prioity queue object
class PriorityQueue{
  //fileds
  private PriorityNode head;
  private int size;
  
  //defualt method intializes the fileds
  public PriorityQueue(){
    head = null;
    size = 0;
  }

  //checks if the queue is empty
  public boolean isEmpty(){
    return head == null;
  }

  //adds a node to the prioity queue in the order of highest priority first
  //Parama:
  //  name - name of node
  //  priority- priority of node
  public void enqueue(String name, int priority){
    // creates temp node to be inserted
    PriorityNode temp = new PriorityNode(name, priority);
    //checks if queue is empty
    if(head == null){
      head = temp; // sets temp to head
    }

    else{
      //checks if the priority of temp is higher than the prioryt in head
      if(head.getPriority() > temp.getPriority()){
        temp.setNext(head);// points temps next refernce to head
        head = temp;// sets head to temp
      }

      //chekcs if there is more than one node in the list
      else if(head.getNext() == null){
        head.setNext(temp); // points head next to temp
      }

      else{
        PriorityNode current = head; // current refernces the current node
        boolean isAdded = false; // checks if temp was adde to the queue
        //loops while current has a next node
        while(current.getNext() != null){
          //checks if the nects nodes priority is less than temps
          if(current.getNext().getPriority() > temp.getPriority()){
            temp.setNext(current.getNext());//poines temps next refernce to currents next reffrence
            current.setNext(temp);// sets current refernces to temp
            isAdded = true;
            break;// breaks out of loop
          }

          else{
            //goes to next node in the queue
            current = current.getNext();
          }
        }

        //checks if temp was never added to the queue
        if(isAdded == false){
          current = temp;//sets current to temp 
        }
      }
    }

    size++;//increases size
  }

  //dequeues the priority queue
  public void dequeue(){
    head = head.getNext();
    size--;
  }

  //Gets the size of the queue
  public int getSize(){
    return size;
  }

  //returns the name the head and then removes it from the list
  public String print(){
    PriorityNode temp = head;
    if(head == null){
      return null;
    }

    head = head.getNext();
    return temp.getName(); 
  }
}