import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product';
import { PageResponse } from '../models/page-response';

@Injectable({
    providedIn: 'root'
})
export class ProductService {

    private readonly productApiUrl =
        `${environment.apiUrl}/products`;

    constructor(
        private http: HttpClient
    ) {
    }

    getActiveProducts(
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

            params = params.set(
                'search',
                search.trim()
            );
        }

        return this.http.get<
            PageResponse<Product>
        >(
            this.productApiUrl,
            {
                params
            }
        );
    }

    getProductById(
        productId: number
    ): Observable<Product> {

        return this.http.get<Product>(
            `${this.productApiUrl}/${productId}`
        );
    }



}