import React from 'react';

const Header = ({ deviceUid }) => {
  return (
    <header className="mb-4">
      <h1 className="text-2xl font-bold text-white">Trợ lý Vườn "Synthia"</h1>
      <p className="text-sm text-gray-400">
        Thiết bị:
        <span id="device-uid" className="font-medium text-cyan-300"> {deviceUid}</span>
      </p>
    </header>
  );
};

export default Header;