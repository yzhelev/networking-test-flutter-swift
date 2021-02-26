//
//  NetworkingApi.swift
//  Runner
//
//  Created by Yovko Zhelev on 24.02.21.
//

import Foundation

class NetworkingApi {
    
    func request(
        method: String? = "GET",
        urlString: String,
        parameters: [String: Any]?,
        headers: [String: String]?,
        completion: @escaping (Any?, String?) -> Void
    ) {
        //create the url with NSURL
        guard let url = URL(string: urlString) else {
            completion(nil, "Error: No url or an invalid url was provided")
            return
        }
        
        //create the session object
        let session = URLSession.shared
        
        //now create the Request object using the url object
        var request = URLRequest(url: url)
        
        //set http method
        request.httpMethod = method
        
        if let bodyParams = parameters {
            do {
                // pass dictionary to data object and set it as request body
                request.httpBody = try JSONSerialization.data(
                    withJSONObject: bodyParams,
                    options:.prettyPrinted
                )
            } catch let error {
                completion(nil, error.localizedDescription)
            }
        }
        
        //Set HTTP Headers
        if let safeHeaders = headers, !safeHeaders.isEmpty {
            for header in safeHeaders {
                request.setValue(header.value, forHTTPHeaderField: header.key)
            }
        }
        
        
        //create dataTask using the session object to send data to the server
        let task = session.dataTask(with: request, completionHandler: { data, response, error in
            guard error == nil else {
                completion(nil, error?.localizedDescription)
                return
            }
            
            guard let data = data else {
                completion(nil, "Data is null")
                return
            }
            
            do {
                //create json object from data
                let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments)
                if let resp = json as? [String: Any] {
                    print(resp)
                }
                completion(json, nil)
            } catch let error {
                completion(nil, error.localizedDescription)
            }
        })
        task.resume()
    }
    
}
