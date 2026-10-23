import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Navbar
} from '../../shared/navbar/navbar';

import {
  Product
} from '../../core/models/product';

import {
  ProductService
} from '../../core/services/product.service';

import {
  PurchaseService
} from '../../core/services/purchase.service';

import {
  PurchaseRequest
} from '../../core/models/purchase-request';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Navbar
  ],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {

  products: Product[] = [];

  search = '';

  currentPage = 0;

  pageSize = 10;

  totalElements = 0;

  totalPages = 0;

  first = true;

  last = true;

  loading = false;

  errorMessage = '';

  successMessage = '';

  purchaseErrorMessage = '';

  quantities: Record<number, number> = {};

  constructor(
    private productService: ProductService,
    private purchaseService: PurchaseService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {

    this.loadProducts();

  }

  loadProducts(): void {

    this.loading = true;

    this.errorMessage = '';

    this.productService
      .getActiveProducts(
        this.search,
        this.currentPage,
        this.pageSize
      )
      .subscribe({

        next: response => {

          this.products = [
            ...response.content
          ];

          this.currentPage =
            response.page;

          this.pageSize =
            response.size;

          this.totalElements =
            response.totalElements;

          this.totalPages =
            response.totalPages;

          this.first =
            this.currentPage === 0;

          this.last =
            this.totalPages === 0
            ||
            this.currentPage >=
            this.totalPages - 1;

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        },

        error: error => {

          console.error(
            'Unable to load products:',
            error
          );

          this.errorMessage =
            'Unable to load products. '
            + 'Please try again.';

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        }

      });

  }

  searchProducts(): void {

    this.currentPage = 0;

    this.loadProducts();

  }

  clearSearch(): void {

    this.search = '';

    this.currentPage = 0;

    this.loadProducts();

  }

  previousPage(): void {

    if (!this.first) {

      this.currentPage--;

      this.loadProducts();

    }

  }

  nextPage(): void {

    if (!this.last) {

      this.currentPage++;

      this.loadProducts();

    }

  }

  purchaseProduct(
    product: Product
  ): void {

    this.clearMessages();

    const quantity =
      Number(this.quantities[product.productId] ?? 1);

    if (
      !Number.isInteger(quantity)
      ||
      quantity <= 0
    ) {
      this.showPurchaseError(
        'Quantity must be a whole number greater than zero'
      );
      return;
    }

    if (quantity > product.quantity) {
      this.showPurchaseError(
        `Only ${product.quantity} item(s) are available`
      );
      return;
    }

    const request: PurchaseRequest = {
      productId: product.productId,
      quantity
    };

    this.purchaseService
      .purchase(request)
      .subscribe({

        next: response => {

          this.showSuccess(
            response.message
            || 'Purchase completed successfully'
          );

          this.quantities[product.productId] = 1;

          this.loadProducts();

        },

        error: error => {

          console.error(error);

          this.showPurchaseError(
            this.extractError(
              error,
              'Purchase failed. Please try again.'
            )
          );

        }

      });

  }

  clearMessages(): void {

    this.successMessage = '';

    this.purchaseErrorMessage = '';

  }

  private showSuccess(
    message: string
  ): void {

    this.successMessage = message;

    this.purchaseErrorMessage = '';

    this.changeDetectorRef
      .detectChanges();

    setTimeout(
      () => {

        this.successMessage = '';

        this.changeDetectorRef
          .detectChanges();

      },
      4000
    );

  }

  private showPurchaseError(
    message: string
  ): void {

    this.purchaseErrorMessage = message;

    this.successMessage = '';

    this.changeDetectorRef
      .detectChanges();

  }

  private extractError(
    error: any,
    fallback: string
  ): string {

    if (
      typeof error.error === 'string'
    ) {
      return error.error;
    }

    if (error.error?.message) {
      return error.error.message;
    }

    if (error.error?.errors) {

      const firstKey =
        Object.keys(
          error.error.errors
        )[0];

      if (firstKey) {
        return error.error.errors[firstKey];
      }

    }

    return fallback;

  }

}