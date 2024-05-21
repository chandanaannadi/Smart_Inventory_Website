import React from 'react';
import './Dashboard.css';

function Home() {
  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>Smart Inventory</h2>
        </div>
        <nav className="sidebar-nav">
          <ul>
            <li>Dashboard</li>
            <li className="active">Inventory</li>
            <li>Requests</li>
            <li>Shipping</li>
            <li>Live Chat</li>
            <li>Manage Users</li>
            <li>Settings</li>
            <li>Log Out</li>
          </ul>
        </nav>
      </aside>
      <main className="main-content">
        <header className="header">
          <input type="text" placeholder="Search product, supplier, order" />
          <div className="user-info">
            <span>🔔</span>
            <span className="user-avatar">👤</span>
          </div>
        </header>
        <section className="inventory-overview">
          <div className="overview-item">
            <h3>Packages</h3>
            <p>14</p>
            <small>Last Month</small>
          </div>
          <div className="overview-item">
            <h3>Total Products</h3>
            <p>868</p>
            <small>₹25000 Revenue</small>
          </div>
          <div className="overview-item">
            <h3>Shipping</h3>
            <p>5</p>
            <small>₹2500 Cost</small>
          </div>
          <div className="overview-item">
            <h3>Low Stocks</h3>
            <p>12</p>
            <small>2 Not in stock</small>
          </div>
        </section>
        <section className="products-section">
          <header className="products-header">
            <h2>Products</h2>
            <div>
              <button>Add Product</button>
              <button>Filters</button>
              <button>Download all</button>
            </div>
          </header>
          <table className="products-table">
            <thead>
              <tr>
                <th>Products</th>
                <th>Tracking Number</th>
                <th>Quantity</th>
                <th>Warehouse id</th>
                <th>Delivery Date</th>
                <th>Availability</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>iphone</td>
                <td>1D3F2E3R4E5R7T8U</td>
                <td>43 Packets</td>
                <td>1A234</td>
                <td>11/12/24</td>
                <td className="in-stock">In-stock</td>
              </tr>
              <tr>
                <td>TV's</td>
                <td>2S3W4A5A6T7Y1S2A</td>
                <td>22 Packets</td>
                <td>1B267</td>
                <td>21/12/24</td>
                <td className="out-of-stock">Out of stock</td>
              </tr>
              <tr>
                <td>Chairs</td>
                <td>2Q3W5A6F7S8H9S25</td>
                <td>36 Packets</td>
                <td>2A354</td>
                <td>5/12/24</td>
                <td className="in-stock">In-stock</td>
              </tr>
              <tr>
                <td>Waching Machine's</td>
                <td>3S5F4D5F6T7Y8Y6R</td>
                <td>14 Packets</td>
                <td>6A546</td>
                <td>8/12/24</td>
                <td className="out-of-stock">Out of stock</td>
              </tr>
              <tr>
                <td>Fridges</td>
                <td>5S6V7C78N9D9M3Z</td>
                <td>5 Packets</td>
                <td>5C342</td>
                <td>9/1/24</td>
                <td className="in-stock">In-stock</td>
              </tr>
              <tr>
                <td>Harpic</td>
                <td>3A5D6X7C8N38N4X</td>
                <td>10 Packets</td>
                <td>5B435</td>
                <td>9/12/24</td>
                <td className="in-stock">In-stock</td>
              </tr>
              <tr>
                <td>Ariel</td>
                <td>5D6F7X7V8A9G3F4D</td>
                <td>23 Packets</td>
                <td>2B234</td>
                <td>15/12/24</td>
                <td className="out-of-stock">Out of stock</td>
              </tr>
              <tr>
                <td>Scotch Brite</td>
                <td>4S5X6C6V7B8N0N8M</td>
                <td>43 Packets</td>
                <td>1C234</td>
                <td>6/6/23</td>
                <td className="in-stock">In-stock</td>
              </tr>
              <tr>
                <td>Coca cola</td>
                <td>2A3D4S5F6Z7C7B8H</td>
                <td>41 Packets</td>
                <td>2C098</td>
                <td>11/11/24</td>
                <td className="low-stock">Low stock</td>
              </tr>
            </tbody>
          </table>
          <div className="pagination">
            <button>Previous</button>
            <button>Next</button>
          </div>
        </section>
      </main>
    </div>
  );
}

export default Home;
