import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  RouterLink
} from '@angular/router';

import {
  Navbar
} from '../../shared/navbar/navbar';

import {
  UserResponse
} from '../../core/models/user-response';

import {
  UserService
} from '../../core/services/user.service';

import {
  ProductService
} from '../../core/services/product.service';

import {
  PurchaseService
} from '../../core/services/purchase.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    Navbar
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  user: UserResponse | null = null;

  totalProducts = 0;

  totalPurchases = 0;

  totalSpent = 0;

  loading = false;

  isAdmin = false;

  constructor(
    private userService: UserService,
    private productService: ProductService,
    private purchaseService: PurchaseService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {

    this.loadDashboard();

  }

  loadDashboard(): void {

    this.loading = true;

    this.userService
      .getCurrentUser()
      .subscribe({

        next: response => {

          this.user = response;

          this.isAdmin =
            response.role === 'ADMIN';

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        },

        error: error => {

          console.error(error);

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        }

      });

    this.productService
      .getActiveProducts(
        '',
        0,
        1
      )
      .subscribe({

        next: response => {

          this.totalProducts =
            response.totalElements;

          this.changeDetectorRef
            .detectChanges();

        },

        error: error => {

          console.error(error);

        }

      });

    this.purchaseService
      .getMyPurchases(
        '',
        '',
        '',
        0,
        100
      )
      .subscribe({

        next: response => {

          this.totalPurchases =
            response.totalElements;

          this.totalSpent =
            response.content
              .reduce(
                (sum, purchase) =>
                  sum + purchase.totalAmount,
                0
              );

          this.changeDetectorRef
            .detectChanges();

        },

        error: error => {

          console.error(error);

        }

      });

  }

  getGreeting(): string {

    const hour =
      new Date().getHours();

    if (hour < 12) {
      return 'Good morning';
    }

    if (hour < 17) {
      return 'Good afternoon';
    }

    return 'Good evening';

  }

}