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
} from '../../../shared/navbar/navbar';

import {
  PurchaseResponse
} from '../../../core/models/purchase-response';

import {
  PurchaseService
} from '../../../core/services/purchase.service';

@Component({
  selector: 'app-purchase-history',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Navbar
  ],
  templateUrl: './purchase-history.html',
  styleUrl: './purchase-history.css'
})
export class PurchaseHistory
  implements OnInit {

  purchases: PurchaseResponse[] = [];

  productName = '';

  fromDate = '';

  toDate = '';

  currentPage = 0;

  pageSize = 10;

  totalElements = 0;

  totalPages = 0;

  first = true;

  last = true;

  loading = false;

  errorMessage = '';

  filterErrorMessage = '';

  constructor(
    private purchaseService:
      PurchaseService,

    private changeDetectorRef:
      ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {

    this.loadPurchases();
  }

  loadPurchases(): void {

    if (!this.isDateRangeValid()) {
      return;
    }

    this.loading = true;

    this.errorMessage = '';

    this.filterErrorMessage = '';

    this.purchaseService
      .getMyPurchases(
        this.productName,
        this.fromDate,
        this.toDate,
        this.currentPage,
        this.pageSize
      )
      .subscribe({

        next: response => {

          this.purchases = [
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
            'Unable to load purchase history:',
            error
          );

          this.loading = false;

          if (error.status === 401) {

            this.errorMessage =
              'Your session has expired. '
              + 'Please log in again.';

          } else {

            this.errorMessage =
              'Unable to load purchase history. '
              + 'Please try again.';
          }

          this.changeDetectorRef
            .detectChanges();
        }

      });
  }

  applyFilters(): void {

    this.currentPage = 0;

    this.loadPurchases();
  }

  clearFilters(): void {

    this.productName = '';

    this.fromDate = '';

    this.toDate = '';

    this.filterErrorMessage = '';

    this.currentPage = 0;

    this.loadPurchases();
  }

  previousPage(): void {

    if (!this.first) {

      this.currentPage--;

      this.loadPurchases();
    }
  }

  nextPage(): void {

    if (!this.last) {

      this.currentPage++;

      this.loadPurchases();
    }
  }

  private isDateRangeValid(): boolean {

    const onlyOneDateProvided =
      (
        this.fromDate
        &&
        !this.toDate
      )
      ||
      (
        !this.fromDate
        &&
        this.toDate
      );

    if (onlyOneDateProvided) {

      this.filterErrorMessage =
        'Please select both From Date '
        + 'and To Date.';

      return false;
    }

    if (
      this.fromDate
      &&
      this.toDate
      &&
      this.fromDate > this.toDate
    ) {

      this.filterErrorMessage =
        'From Date cannot be after To Date.';

      return false;
    }

    return true;
  }
}