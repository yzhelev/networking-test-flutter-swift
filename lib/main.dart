import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final MethodChannel _methodChannel = const MethodChannel("com.bitpioneers.networking");

  final String url = "https://jsonplaceholder.typicode.com/todos/1";
  Map<dynamic, dynamic> user;

  Future<Map<dynamic, dynamic>> getUser() async {
    final Map<String, dynamic> body = {
      "title": "our man",
      "body": "body example",
      "userId": 12,
    };

    final Map<String, dynamic> headers = {
      "Content-type": "application/json; charset=UTF-8",
    };


    try {
      final dynamic response = await _methodChannel.invokeMethod("request", {
        "url": url,
        "method": "GET",
        "parameters": body,
        "headers": headers
      });

      debugPrint(response.toString());

      return response;
    } on PlatformException catch (e) {
      debugPrint(e.message + " " + e.details);
    } catch (e) {
      debugPrint(e.toString());
    }

    return {};
  }

  String parseUserToString(Map<dynamic, dynamic> user) {
    String text = "";
    for (var entry in user.entries) {
      text += "${entry.key}: ${entry.value}\n";
    }

    return text;
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              user != null ? "User info:" : "Press the button to get user info",
              style: Theme.of(context).textTheme.headline5,
            ),
            SizedBox(height: 30,),
            Text(
              user != null ? parseUserToString(user) : "",
              style: Theme.of(context).textTheme.bodyText1,
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 60,),
            FlatButton(
              color: Colors.green,
              child: Text(
                "GET USER",
                style: TextStyle(color: Colors.white),
              ),
              onPressed: () async {
                user = await getUser();
                setState(() {});
              }
            )
          ],
        ),
      ),
    );
  }
}
