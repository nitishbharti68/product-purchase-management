import {
  Component
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  ReactiveFormsModule,
  FormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';

import {
  Navbar
} from '../../../shared/navbar/navbar';

import {
  AdminProductService
} from '../../../core/services/admin-product.service';

import {
  Product
} from '../../../core/models/product';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    Navbar
  ],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css'
})
export class AdminProducts {

  productForm;

  products: Product[] = [];

  search = '';

  loading = false;

  editMode = false;

  editingProductId: number | null = null;

  successMessage = '';

  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private adminProductService:
      AdminProductService
  ) {

    this.productForm =
      this.fb.group({

        productName: [
          '',
          Validators.required
        ],

        description: [
          '',
          Validators.required
        ],

        price: [
          0,
          Validators.required
        ],

        quantity: [
          0,
          Validators.required
        ],

        category: [''],

        imagePath: ['']

      });

    this.loadProducts();

  }

  loadProducts(): void {

    this.loading = true;

    this.adminProductService
      .getProducts(
        this.search,
        0,
        50
      )
      .subscribe({

        next: response => {

          this.products =
            response.content;

          this.loading = false;

        },

        error: error => {

          console.error(error);

          this.loading = false;

          this.showError(
            this.extractError(
              error,
              'Unable to load products'
            )
          );

        }

      });

  }

  searchProducts(): void {

    this.loadProducts();

  }

  saveProduct(): void {

    if (this.productForm.invalid) {
      return;
    }

    const request =
      this.productForm.getRawValue() as any;

    if (
      this.editMode
      &&
      this.editingProductId
    ) {

      this.adminProductService
        .updateProduct(
          this.editingProductId,
          request
        )
        .subscribe({

          next: () => {

            this.showSuccess(
              'Product updated successfully'
            );

            this.cancelEdit();

            this.loadProducts();

          },

          error: error => {

            console.error(error);

            this.showError(
              this.extractError(
                error,
                'Unable to update product'
              )
            );

          }

        });

      return;
    }

    this.adminProductService
      .addProduct(request)
      .subscribe({

        next: () => {

          this.showSuccess(
            'Product added successfully'
          );

          this.productForm.reset();

          this.loadProducts();

        },

        error: error => {

          console.error(error);

          this.showError(
            this.extractError(
              error,
              'Unable to add product'
            )
          );

        }

      });

  }

  editProduct(
    product: Product
  ): void {

    this.editMode = true;

    this.editingProductId =
      product.productId;

    this.clearMessages();

    this.productForm.patchValue({

      productName:
        product.productName,

      description:
        product.description,

      price:
        product.price,

      quantity:
        product.quantity,

      category:
        product.category ?? '',

      imagePath:
        product.imagePath ?? ''

    });

    window.scrollTo(
      {
        top: 0,
        behavior: 'smooth'
      }
    );

  }

  cancelEdit(): void {

    this.editMode = false;

    this.editingProductId = null;

    this.productForm.reset();

  }

  toggleStatus(
    product: Product
  ): void {

    const newStatus =
      !product.active;

    const action =
      newStatus
        ? 'activate'
        : 'deactivate';

    const confirmed =
      confirm(
        `Are you sure you want to ${action} `
        + `"${product.productName}"?`
      );

    if (!confirmed) {
      return;
    }

    this.adminProductService
      .updateProductStatus(
        product.productId,
        newStatus
      )
      .subscribe({

        next: () => {

          this.showSuccess(
            `Product ${action}d successfully`
          );

          this.loadProducts();

        },

        error: error => {

          console.error(error);

          this.showError(
            this.extractError(
              error,
              'Unable to update product status'
            )
          );

        }

      });

  }

  clearMessages(): void {

    this.successMessage = '';

    this.errorMessage = '';

  }

  private showSuccess(
    message: string
  ): void {

    this.successMessage = message;

    this.errorMessage = '';

    setTimeout(
      () => {
        this.successMessage = '';
      },
      4000
    );

  }

  private showError(
    message: string
  ): void {

    this.errorMessage = message;

    this.successMessage = '';

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