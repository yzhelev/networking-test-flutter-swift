import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
    
    let methodChannel = FlutterMethodChannel(name: "com.bitpioneers.networking", binaryMessenger: controller.binaryMessenger)
    
    let networkingApi = NetworkingApi()
    
    methodChannel.setMethodCallHandler({
      (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
      
        executeRequest: if call.method == "request" {
            guard let args = call.arguments as? Dictionary<String, Any> else {
                break executeRequest
            }
            
            if let url = args["url"] as? String {
                let method: String? = args["method"] as? String
                let params: [String: Any]? =  args["parameters"] as? [String: Any]
                let headers: [String: String]? =  args["headers"] as? [String: String]
                
                networkingApi.request(method: method, urlString: url, parameters: params, headers: headers) { response, error in
                    if let res = response {
                        result(res)
                    }
                    
                    if let err = error {
                        result(err)
                    }
                }
            }
        }
    })
    
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
