#TCP Simulator
###TCP Simulation Using UDP

###Abstract


The objective of this project was to implement the Transmission Control Protocol (TCP) through the use of the User Datagram Protocol (UDP). In order to accomplish this, packets that were sent between client and server needed to be verified for any potential errors that occurred between the transfer. Lost packets also needed to be re-sent to guarantee delivery of data in the correct order. Since the client and server were tested on a closed loop (localhost), a Scrambler needed to be implemented which would intercept the messages between the client and server and simulate lost and scrambled packets.

###Architecture

The <b>Client</b> was responsible for sending (uploading) data to the server. In order to do this, the client would take a string message as a parameter and break it up into multiple strings of 10 characters. This was the arbitrarily assigned data size available in each packet. Each of these smaller messages were then inserted into a packet which would be sent off to the server (more on the packet structure below). The client would attempt to send the message to the server and wait 1 second for a response form the server. If no acknowledgment was received in that period, the same packet would be sent. When an acknowledgment from the server was successfully received, the client would ensure that the server received the correct packet as well as a byte indicating that the server received the correct packet. This was done using a validation byte which would return 0 for invalid and 1 for valid. If the client received a scrambled packet, it would re-send the packet. Once all of the packets, except for the last packet, were sent successfully, the client would send the final packet with a unique packet value which indicated that the data transfer was complete. After receiving the acknowledgement for the final packet, the client would terminate.

The <b>Server</b> was responsible for receiving data from the client. When a new client would send a packet, the server would open the packet and determine if the data was valid. Several checks were made to ensure the integrity of the packet. The first check was to make sure the packet came from the correct client. If multiple clients were sending messages simultaneously, the server would only handle one client at a time. The second check was to make sure that the validation byte was set to 1. Otherwise, the packet was faulty. The third check was to make sure that the correct packet was received. That is, the packet is the comes after the preceding packet. The fourth and final check was to ensure that the message data correctly matched the checksum. Once all checks were made, the server would send an acknowledgment indicating that the packet was received. In the event that two identical packets were received, the server would send an acknowledgment but disregard the duplicate. If any of the checks failed, a message informing the client was sent; requesting the client to send the packet again. When the final packet was received, the server would stop listening for messages from that client and wait for any other incoming client messages.

The <b>Scrambler</b> was responsible for simulating the transfer of data between clients and the server. The scrambler would listen for any incoming client, extract the packet, send the packet to the server, wait for the server to respond, and send the server packet to the client. Between the transfer of packets, the scrambler would randomly choose whether to lose, scramble, or simply send the packet to the corresponding recipient. The probability that any of these events occurred were set before initializing the scrambler. That is, the probabilities were determined by the user. For testing, the loss and scrambling of packets occurred about 20 to 40 percent of the time. To simulate a loss packet, the packet would not be sent to the recipient and the scramble would wait for the sender to redeliver the packet. To simulate a scrambled packet, the scrambler would shuffle the bytes inside the packet, send it off to the recipient, and wait for a response. Otherwise, the packet would be sent to the recipient.

The <b>CheckSum</b> was achieved by using a hash function called CRC32 offered by the Java utilities library. This function would take the 10 character string and return a unique hash value. When the message was sent, the recipient would run the string through the same hash function and compare the output with the hash number contained in the packet. If the values were equivalent, then the packet was not damaged.

<b>Packet Structure:</b>

	Validation		: set to 0 when sent packet was invalid, set to 1 otherwise
	Source			: client port ID
	Packet ID		: packet number ranging from 1-9999. 0 used to indicate final packet
	Data			: 10 character message to be delivered
	CheckSum		: hash number for verifying the integrity of the message


###Inputs/Outputs for Testing

To run the simulator, open 3 terminals and navigate to the folder containing the Client.java, Server.java, and Scrambler.java files. If the class files are not present, simply compile the java code. Then do the following:
Step 1: In one of the terminals Run the Scrambler.java file
Step 2: In another terminal, run the Server.java file
Step 3: In the last terminal, run the Client.java file
Step 4: To run multiple clients simultaneously, open another terminal and run Client.java.

Note: To send a unique message, type: Client.java “message to send”


###Conclusion

Overall, the project was a success. No matter how many packets are lost or scrambled, all of the packets are guaranteed to be delivered successfully without errors. The server is even capable of handling multiple clients simultaneously. 

