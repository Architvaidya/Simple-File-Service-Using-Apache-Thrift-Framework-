Name: Archit Vaidya

programming Language: Java

Implementation Details

1)Used Hashmap to store the owner and his files. Owner is the key and list of files is the value.

2)When we start the server a folder called Root is created. All the folders of owners are present in Root.


Steps to setup the environment:

Steps for environment setup:

1) Extract avaidya4-project1.tar.gz
2) Go to the directory avaidya4-project1
3) Enter command "bash"
4) source source
5) chmod a+x server.sh
6) chmod a+x client.sh

Steps to compile:

1) make

Steps to Run the program:

1) For server: ./server.sh <port_number>
2) For client: ./cleint.sh <hostname> <port_number> --operation <operation_type> --filename <filename> --user <user_name>


Sample output:

remote02:~/DS/avaidya4-project1> ./client.sh remote01.cs.binghamton.edu 9090 --operation read --filename abc.txt --user guest
{"1":{"rec":{"1":{"str":"abc.txt"},"2":{"i64":1475797663000},"3":{"i64":1475797663000},"4":{"i32":0},"5":{"str":"guest"},"6":{"i32":15},"7":{"str":"0e0d11c32ed3b89e790aaa1e601552dd"}}},"2":{"str":"This is Archit\n"}}

remote01:~/DS/avaidya4-project1> ./client.sh remote00.cs.binghamton.edu 4242 --operation write --filename abc.txt --user guest
{"1":{"i32":1}}

remote01:~/DS/avaidya4-project1> ./client.sh remote00.cs.binghamton.edu 4242 --operation list --user guest
["rec",1,{"1":{"str":"abc.txt"},"2":{"i64":1475799418000},"3":{"i64":1475799418000},"4":{"i32":0},"5":{"str":"guest"},"6":{"i32":15},"7":{"str":"0e0d11c32ed3b89e790aaa1e601552dd"}}
]
