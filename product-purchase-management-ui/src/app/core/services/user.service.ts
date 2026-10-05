import { Injectable } from '@angular/core';

import {
    HttpClient
} from '@angular/common/http';

import {
    Observable
} from 'rxjs';

import {
    environment
} from '../../../environments/environment';

import {
    ChangePasswordRequest
} from '../models/change-password-request';

import {
    UserResponse
} from '../models/user-response';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private readonly userApiUrl =
        `${environment.apiUrl}/users`;

    constructor(
        private http: HttpClient
    ) {
    }

    getCurrentUser(): Observable<UserResponse> {

        return this.http.get<UserResponse>(
            `${this.userApiUrl}/me`
        );

    }

    changePassword(
        request: ChangePasswordRequest
    ): Observable<any> {

        return this.http.put(
            `${this.userApiUrl}/me/password`,
            request
        );

    }

}