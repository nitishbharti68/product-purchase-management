import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { LoginRequest } from '../models/login-request';

import { LoginResponse } from '../models/login-response';

import { environment } from '../../../environments/environment';
import { RegisterRequest } from '../models/register-request';

import { RegisterResponse } from '../models/register-response';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(
        private http: HttpClient
    ) {
    }

    login(
        request: LoginRequest
    ): Observable<LoginResponse> {

        return this.http.post<LoginResponse>(
            `${environment.apiUrl}/auth/login`,
            request
        );

    }

    register(
        request: RegisterRequest
    ) {

        return this.http.post<RegisterResponse>(
            `${environment.apiUrl}/auth/register`,
            request
        );

    }

    isLoggedIn(): boolean {

        return !!localStorage.getItem(
            'token'
        );

    }


    isAdmin(): boolean {

        return localStorage.getItem(
            'role'
        ) === 'ADMIN';

    }

}