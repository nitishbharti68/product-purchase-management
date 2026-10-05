import { Injectable } from '@angular/core';

import {
    HttpClient,
    HttpParams
} from '@angular/common/http';

import { Observable } from 'rxjs';

import {
    environment
} from '../../../environments/environment';

import {
    ProductRequest
} from '../models/product-request';

import {
    Product
} from '../models/product';

import {
    PageResponse
} from '../models/page-response';

@Injectable({
    providedIn: 'root'
})
export class AdminProductService {

    private readonly apiUrl =
        `${environment.apiUrl}/admin/products`;

    constructor(
        private http: HttpClient
    ) {
    }

    addProduct(
        request: ProductRequest
    ): Observable<Product> {

        return this.http.post<Product>(
            this.apiUrl,
            request
        );

    }

    getProducts(
        search: string,
        page: number,
        size: number
    ): Observable<PageResponse<Product>> {

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

        if (search.trim()) {

            params =
                params.set(
                    'search',
                    search.trim()
                );
        }

        return this.http.get<
            PageResponse<Product>
        >(
            this.apiUrl,
            {
                params
            }
        );

    }

    updateProduct(
        productId: number,
        request: ProductRequest
    ): Observable<Product> {

        return this.http.put<Product>(
            `${this.apiUrl}/${productId}`,
            request
        );

    }

    updateProductStatus(
        productId: number,
        active: boolean
    ): Observable<Product> {

        return this.http.patch<Product>(
            `${this.apiUrl}/${productId}/status`,
            {
                active
            }
        );

    }

}