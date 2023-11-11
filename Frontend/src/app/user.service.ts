import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  url = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }

  signup(data: { name: any; email: any; contactNumber: any; password: any; }) {
    return this.httpClient.post(this.url + "/user/signup", data, {
      headers: new HttpHeaders().set('Content-type', 'application/json')
    })
  }

  forgotPassword(data: any) {
    return this.httpClient.post(this.url + "/user/forgot-password", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    })
  }
  changePassword(data: any) {
    return this.httpClient.post(this.url + "/user/change-password", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    })
  }

  login(data: any) {
    return this.httpClient.post(this.url + "/user/login", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    })
  }
  checkToken() {
    return this.httpClient.get(this.url + "/user/check-token");
  }

  getUsers(){
    return this.httpClient.get(this.url + "/user/get");
  }

  update(data:any){
    return this.httpClient.post(this.url + "/user/update" , data , {
      headers:new HttpHeaders().set('Content-Type' , 'application/json')
    })
  }
}
