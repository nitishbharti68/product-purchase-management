import {
    Injectable
} from '@angular/core';

import {
    HttpClient,
    HttpParams
} from '@angular/common/http';

import {
    Observable
} from 'rxjs';

import {
    environment
} from '../../../environments/environment';

import {
    PurchaseRequest
} from '../models/purchase-request';

import {
    PurchaseResponse
} from '../models/purchase-response';

import {
    PageResponse
} from '../models/page-response';

@Injectable({
    providedIn: 'root'
})
export class PurchaseService {

    private readonly purchaseApiUrl =
        `${environment.apiUrl}/purchases`;

    constructor(
        private http: HttpClient
    ) {
    }

    purchase(
        request: PurchaseRequest
    ): Observable<PurchaseResponse> {

        return this.http.post<PurchaseResponse>(
            this.purchaseApiUrl,
            request
        );
    }

    getMyPurchases(
        productName: string,
        fromDate: string,
        toDate: string,
        page: number,
        size: number
    ): Observable<PageResponse<PurchaseResponse>> {

        let params =
            new HttpParams()
                .set(
                    'page',
                    page.toString()
                )
                .set(
                    'size',
                    size.toString()
                );

        if (productName.trim()) {

            params = params.set(
                'productName',
                productName.trim()
            );
        }

        if (
            fromDate
            &&
            toDate
        ) {

            params = params
                .set(
                    'fromDate',
                    fromDate
                )
                .set(
                    'toDate',
                    toDate
                );
        }

        return this.http.get<
            PageResponse<PurchaseResponse>
        >(
            `${this.purchaseApiUrl}/me`,
            {
                params
            }
        );
    }

    getMyPurchaseById(
        purchaseId: number
    ): Observable<PurchaseResponse> {

        return this.http.get<PurchaseResponse>(
            `${this.purchaseApiUrl}/me/${purchaseId}`
        );
    }
}