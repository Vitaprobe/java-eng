# Solution compiled in Java
Tested in Playground enviroment and works great!


# Java implementation of core EET functionalities

There are no other dependences but java runtime (at least 1.4.2.)

List of features:

* build a sale registration based on business data
* generate PKP/BKP for receipt printing
* generate valid signed SOAP message
* send the request to the playground endpoint
* receive response containing FIK
* setup custom trust manager defaulting to included JKS with the right CA certificates


# Build 

To build this solution, clone this repository and then use [gradle](https://gradle.org/) to build it.
```
git clone https://github.com/Vitaprobe/java-eng.git
cd java-eng/
./gradlew jar
```
Then navigate to eet-java/build/libs/ and run eet-java.jar or use it in your builds.
To test it out, you can use this example:
```
cd eet-java/build/libs/
java -jar eet-java.jar
```
At this point you can experience trouble with file signing. There are templates needed to be used which for some reason has to be perserved in a zip package otherwise they change their signature and the response from Electronic Evidence of Sales will be invalid.
Therfore unzip the file "templates" in /resources and replace all files with the extracted ones.
```
cd eet-java/src/main/resources/eetjava
unzip templates.zip -d templates
```
Now you have to rebuild the solution.

If you have followed all steps, you can now test it at your site.
The test will produce output to the console, containing mainly FIK which is the important code used for evidence of sale.
The output could look like this:
```
...
penv:Body wsu:Id="Body-9822c666-08ab-43ac-91e1-2ece09f0dfb9"><eet:Odpoved><eet:Hlavicka uuid_zpravy="de25a63b-5180-4c5e-8630-6973210443c8" bkp="E4765BD6-D3847C10-FA32CB58-63AE09E4-75682A4D" dat_prij="2017-03-02T11:16:36+01:00"/><eet:Potvrzeni fik="faffb4e5-2908-44ae-925d-445e0c01e0d8-ff" test="true"/></eet:Odpoved></soapenv:Body></soapenv:Envelope>
===== END EET RESPONSE =====
===== BKP: E4765BD6-D3847C10-FA32CB58-63AE09E4-75682A4D

===== PKP: hKEj4ZQusCcU+2TEkb5TTGjEqCHgIac8xjzpgF3r4cOMlS77KvbArp668kDBg6Qdpkk/wAuCPfdKNTq7iOxB4i2urw4qnH/C701yL+GCtYJ94sUF9Q1oL7VI973zzkSucvy6JArToJgzBH19QAbEIFhWpbq8AKK+ScqJeVsL+d+765FpLuBMuHqEDVybJgVf9G9YbUylSz3z/ejlv8yOSbEp1YC4/6tcQyHxiuyqDpurwxzlRFXNjtdb5BhESgp917RP4aG9YbAEF2XDfWqpHJ4Za6Geu1bepeh5Uow7D5CCz72PylQsZuNj/KJE/gOgrZKXhDJCY7VBxxGPGufuDQ==

===== FIK: faffb4e5-2908-44ae-925d-445e0c01e0d8-ff
```



# Basic usage

```java
public static void main(String[] args) {
    try {
        EetRegisterRequest request=EetRegisterRequest.builder()
            .dic_popl("CZ00000019") // DIC - tax indentification number of merchant
            .id_provoz("1") // ID of sales place
            .id_pokl("POKLADNA01") // ID of cash register
            .porad_cis("1") // ID of trade or sale
            .dat_trzby(EetRegisterRequest.formatDate(new Date())) // Date of transaction
            .celk_trzba(100.0) // Price in CZK - this will be updated as we progress with the implementation
            .rezim(0) // Mode of sale, 0 is standard
            .pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/eetjava/EET_CA1_Playground-CZ00000019.p12"))) // Certificate used to identify merchant
            .pkcs12password("eet") // Password for the certificate
            .build();

        //for receipt printing in online mode
        String bkp=request.formatBkp();

        //for receipt printing in offline mode
        String pkp=request.formatPkp();
        //the receipt can be now stored for offline processing

        //try send
        String requestBody=request.generateSoapRequest();
        System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

        String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
        System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
        Pattern pattern = Pattern.compile("Potvrzeni fik=\"(.*)\" ");
        Matcher matcher = pattern.matcher(response);
        matcher.find();
        String fik = matcher.group(1);
        // Print out important details
        System.out.printf("===== BKP: %s\n\n", bkp);
        System.out.printf("===== PKP: %s\n\n", pkp);
        System.out.printf("===== FIK: %s\n\n", fik);
    }
    catch (Exception e){
        e.printStackTrace();
    }
}
```

